-- ==================================
-- 🏎️ DRIVER TABLE
-- ==================================
DROP TABLE IF EXISTS driver;
CREATE TABLE driver
(
    id          BIGSERIAL PRIMARY KEY,
    current_lat DOUBLE PRECISION,
    current_lng DOUBLE PRECISION,
    fcm_token   VARCHAR(255),
    status      VARCHAR(50) NOT NULL
);

-- 📝 Insert sample drivers
INSERT INTO driver (current_lat, current_lng, fcm_token, status)
VALUES (10.762622, 106.660172,
        'c91uiA6XdIJwnvielSWRna:APA91bECAztpRpv_advmdWXUKqfL1BfCsO5fHvnf0brchdj-AWXOrYCifMvMVQ1Yr8Qdd8XIZ64aa7zFek8w9dITZd9WHzNh-sZdW77lT3DfPRMExSoM_Oc',
        'AVAILABLE'),
       (21.027763, 105.834160,
        'c91uiA6XdIJwnvielSWRna:APA91bECAztpRpv_advmdWXUKqfL1BfCsO5fHvnf0brchdj-AWXOrYCifMvMVQ1Yr8Qdd8XIZ64aa7zFek8w9dITZd9WHzNh-sZdW77lT3DfPRMExSoM_Oc',
        'AVAILABLE');
