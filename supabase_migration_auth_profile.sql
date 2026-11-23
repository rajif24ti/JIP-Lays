-- Auth profile trigger and authenticated CRUD access for the mobile schema.
-- Run in the Supabase SQL Editor after the mobile tables have been created.

BEGIN;

CREATE OR REPLACE FUNCTION mobile.sync_auth_profile_from_metadata()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = ''
AS $$
BEGIN
    INSERT INTO mobile.users (id, email, nama, handphone, role)
    VALUES (
        NEW.id,
        NEW.email,
        NEW.raw_user_meta_data ->> 'nama',
        NEW.raw_user_meta_data ->> 'handphone',
        NEW.raw_user_meta_data ->> 'role'
    )
    ON CONFLICT (id) DO UPDATE SET
        email = EXCLUDED.email,
        nama = EXCLUDED.nama,
        handphone = EXCLUDED.handphone,
        role = EXCLUDED.role;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS mobile_sync_profile_from_auth_user ON auth.users;
CREATE TRIGGER mobile_sync_profile_from_auth_user
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION mobile.sync_auth_profile_from_metadata();

ALTER TABLE mobile.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE mobile.karyawan ENABLE ROW LEVEL SECURITY;
ALTER TABLE mobile.produksi ENABLE ROW LEVEL SECURITY;

GRANT USAGE ON SCHEMA mobile TO anon, authenticated;

REVOKE ALL ON TABLE mobile.users FROM PUBLIC, anon, authenticated;
GRANT SELECT ON TABLE mobile.users TO authenticated;
DROP POLICY IF EXISTS "Allow read mobile.users" ON mobile.users;
DROP POLICY IF EXISTS "Allow insert mobile.users" ON mobile.users;
DROP POLICY IF EXISTS "Allow update mobile.users" ON mobile.users;
DROP POLICY IF EXISTS "Read own user profile" ON mobile.users;
CREATE POLICY "Read own user profile" ON mobile.users
    FOR SELECT TO authenticated USING (id = (SELECT auth.uid()));

REVOKE ALL ON TABLE mobile.karyawan, mobile.produksi FROM PUBLIC, anon, authenticated;
GRANT SELECT ON TABLE mobile.karyawan, mobile.produksi TO anon;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE mobile.karyawan, mobile.produksi TO authenticated;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA mobile TO authenticated;

DROP POLICY IF EXISTS "Allow read mobile.karyawan" ON mobile.karyawan;
DROP POLICY IF EXISTS "Allow insert mobile.karyawan" ON mobile.karyawan;
DROP POLICY IF EXISTS "Allow update mobile.karyawan" ON mobile.karyawan;
DROP POLICY IF EXISTS "Allow delete mobile.karyawan" ON mobile.karyawan;
DROP POLICY IF EXISTS "Authenticated access mobile.karyawan" ON mobile.karyawan;
DROP POLICY IF EXISTS "Public read mobile.karyawan" ON mobile.karyawan;
DROP POLICY IF EXISTS "Authenticated karyawan access" ON mobile.karyawan;
DROP POLICY IF EXISTS "Public karyawan read" ON mobile.karyawan;
CREATE POLICY "Authenticated karyawan access" ON mobile.karyawan
    FOR ALL TO authenticated USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Public karyawan read" ON mobile.karyawan
    FOR SELECT TO anon USING (TRUE);

DROP POLICY IF EXISTS "Allow read mobile.produksi" ON mobile.produksi;
DROP POLICY IF EXISTS "Allow insert mobile.produksi" ON mobile.produksi;
DROP POLICY IF EXISTS "Allow update mobile.produksi" ON mobile.produksi;
DROP POLICY IF EXISTS "Allow delete mobile.produksi" ON mobile.produksi;
DROP POLICY IF EXISTS "Authenticated access mobile.produksi" ON mobile.produksi;
DROP POLICY IF EXISTS "Public read mobile.produksi" ON mobile.produksi;
DROP POLICY IF EXISTS "Authenticated produksi access" ON mobile.produksi;
DROP POLICY IF EXISTS "Public produksi read" ON mobile.produksi;
CREATE POLICY "Authenticated produksi access" ON mobile.produksi
    FOR ALL TO authenticated USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Public produksi read" ON mobile.produksi
    FOR SELECT TO anon USING (TRUE);

DROP POLICY IF EXISTS "Public Read menu-produksi" ON storage.objects;
DROP POLICY IF EXISTS "Public Insert menu-produksi" ON storage.objects;
DROP POLICY IF EXISTS "Public Update menu-produksi" ON storage.objects;
DROP POLICY IF EXISTS "Public Delete menu-produksi" ON storage.objects;
DROP POLICY IF EXISTS "Authenticated write menu-produksi" ON storage.objects;
DROP POLICY IF EXISTS "Public menu-produksi read" ON storage.objects;
DROP POLICY IF EXISTS "Authenticated menu-produksi write" ON storage.objects;
CREATE POLICY "Public menu-produksi read" ON storage.objects
    FOR SELECT TO anon, authenticated USING (bucket_id = 'menu-produksi');
CREATE POLICY "Authenticated menu-produksi write" ON storage.objects
    FOR ALL TO authenticated USING (bucket_id = 'menu-produksi')
    WITH CHECK (bucket_id = 'menu-produksi');

COMMIT;
