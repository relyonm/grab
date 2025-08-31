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


-- ==================================
-- 🧑‍🤝‍🧑 PASSENGER TABLE
-- ==================================
DROP TABLE IF EXISTS passenger;
CREATE TABLE passenger
(
    id             BIGSERIAL PRIMARY KEY,
    phone_number   VARCHAR(20) NOT NULL,
    otp            VARCHAR(10),
    otp_expired_at TIMESTAMP WITH TIME ZONE,
    fcm_token      TEXT
);

-- 📝 Insert sample passengers
INSERT INTO passenger (phone_number, otp, otp_expired_at, fcm_token)
VALUES ('+84123456789', '123456', NOW() + INTERVAL '5 minutes', 'fcm_token_1'),
       ('+84987654321', '654321', NOW() + INTERVAL '10 minutes', 'fcm_token_2'),
       ('+84111222333', NULL, NULL, 'fcm_token_3'),
       ('+84999888777', '111111', NOW() + INTERVAL '15 minutes', NULL);


-- ==================================
-- 🚕 RIDE_BOOKING TABLE
-- ==================================
DROP TABLE IF EXISTS ride_booking;
CREATE TABLE ride_booking
(
    id                  BIGSERIAL PRIMARY KEY,
    passenger_id        BIGINT           NOT NULL,
    driver_id           BIGINT,

    -- 📍 Pickup location
    pickup_lat          DOUBLE PRECISION NOT NULL,
    pickup_lng          DOUBLE PRECISION NOT NULL,

    -- 🎯 Dropoff location
    dropoff_lat         DOUBLE PRECISION NOT NULL,
    dropoff_lng         DOUBLE PRECISION NOT NULL,

    status              VARCHAR(50)      NOT NULL,

    -- 🚫 Rejected drivers (array of IDs)
    rejected_driver_ids BIGINT[]
);
