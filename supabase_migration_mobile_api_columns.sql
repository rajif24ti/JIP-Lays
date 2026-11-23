-- Align existing mobile tables with the columns used by the Android REST API.
-- Safe to run repeatedly after the base mobile schema has been created.

BEGIN;

ALTER TABLE mobile.karyawan
    ADD COLUMN IF NOT EXISTS no_telepon TEXT,
    ADD COLUMN IF NOT EXISTS status TEXT DEFAULT 'Aktif',
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE mobile.produksi
    ADD COLUMN IF NOT EXISTS nama_menu TEXT,
    ADD COLUMN IF NOT EXISTS tanggal_produksi TEXT,
    ADD COLUMN IF NOT EXISTS jumlah_porsi INTEGER,
    ADD COLUMN IF NOT EXISTS detail_gizi JSONB,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'karyawan' AND column_name = 'telepon'
    ) THEN
        UPDATE mobile.karyawan
        SET no_telepon = COALESCE(NULLIF(no_telepon, ''), telepon)
        WHERE no_telepon IS NULL OR no_telepon = '';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'karyawan' AND column_name = 'status_keaktifan'
    ) THEN
        UPDATE mobile.karyawan
        SET status = COALESCE(NULLIF(status, ''), status_keaktifan, 'Aktif')
        WHERE status IS NULL OR status = '';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'produksi' AND column_name = 'menu'
    ) THEN
        UPDATE mobile.produksi
        SET nama_menu = NULLIF(menu, '')
        WHERE nama_menu IS NULL OR nama_menu = '';

        ALTER TABLE mobile.produksi ALTER COLUMN menu DROP NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'produksi' AND column_name = 'tanggal'
    ) THEN
        UPDATE mobile.produksi
        SET tanggal_produksi = NULLIF(tanggal, '')
        WHERE tanggal_produksi IS NULL OR tanggal_produksi = '';

        ALTER TABLE mobile.produksi
            ALTER COLUMN tanggal SET DEFAULT TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD');
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'produksi' AND column_name = 'porsi'
    ) THEN
        UPDATE mobile.produksi
        SET jumlah_porsi = CASE
            WHEN porsi::TEXT ~ '^[0-9]+$' THEN porsi::TEXT::INTEGER
            ELSE jumlah_porsi
        END
        WHERE jumlah_porsi IS NULL;

        ALTER TABLE mobile.produksi ALTER COLUMN porsi SET DEFAULT 0;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'produksi' AND column_name = 'gizi'
    ) THEN
        UPDATE mobile.produksi
        SET detail_gizi = gizi
        WHERE detail_gizi IS NULL OR detail_gizi = '{}'::JSONB;

        ALTER TABLE mobile.produksi ALTER COLUMN gizi SET DEFAULT '{}'::JSONB;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'mobile' AND table_name = 'produksi' AND column_name = 'sekolah_tujuan'
    ) THEN
        ALTER TABLE mobile.produksi ALTER COLUMN sekolah_tujuan SET DEFAULT '[]'::JSONB;
    END IF;
END
$$;

CREATE OR REPLACE FUNCTION mobile.set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
SET search_path = ''
AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS set_karyawan_updated_at ON mobile.karyawan;
CREATE TRIGGER set_karyawan_updated_at
    BEFORE UPDATE ON mobile.karyawan
    FOR EACH ROW EXECUTE FUNCTION mobile.set_updated_at();

DROP TRIGGER IF EXISTS set_produksi_updated_at ON mobile.produksi;
CREATE TRIGGER set_produksi_updated_at
    BEFORE UPDATE ON mobile.produksi
    FOR EACH ROW EXECUTE FUNCTION mobile.set_updated_at();

COMMIT;
