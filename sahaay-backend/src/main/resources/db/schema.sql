-- 1. Users table (Main user management)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    date_of_birth DATE,
    gender VARCHAR(20),
    profile_picture_url VARCHAR(255),
    user_type VARCHAR(20) DEFAULT 'GENERAL' CHECK (user_type IN ('GENERAL', 'VOLUNTEER', 'ADMIN')),
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    medical_conditions TEXT,
    accessibility_needs TEXT,
    preferred_language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Volunteers table (Extended info for volunteers)
CREATE TABLE volunteers (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    verification_status VARCHAR(20) DEFAULT 'PENDING' CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    background_check_status VARCHAR(20) DEFAULT 'PENDING',
    availability_hours JSONB, -- Store availability as JSON: {"monday": ["09:00-17:00"], "tuesday": ["10:00-18:00"]}
    max_distance_km INTEGER DEFAULT 10,
    volunteer_since DATE DEFAULT CURRENT_DATE,
    total_completed_tasks INTEGER DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    specializations TEXT[], -- PostgreSQL array for specializations
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Skills table (Master list of skills)
CREATE TABLE skills (
    id SERIAL PRIMARY KEY,
    skill_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(50), -- e.g., 'medical', 'transport', 'household', 'technical'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Volunteer Skills (Many-to-many relationship)
CREATE TABLE volunteer_skills (
    id SERIAL PRIMARY KEY,
    volunteer_id INTEGER REFERENCES volunteers(id) ON DELETE CASCADE,
    skill_id INTEGER REFERENCES skills(id) ON DELETE CASCADE,
    proficiency_level VARCHAR(20) DEFAULT 'BASIC' CHECK (proficiency_level IN ('BASIC', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(volunteer_id, skill_id)
);

-- 5. SOS Alerts table
CREATE TABLE sos_alerts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    alert_type VARCHAR(30) DEFAULT 'EMERGENCY' CHECK (alert_type IN ('EMERGENCY', 'MEDICAL', 'SAFETY', 'FIRE', 'ACCIDENT')),
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    location_address TEXT,
    alert_message TEXT,
    audio_file_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RESPONDED', 'RESOLVED', 'FALSE_ALARM')),
    priority_level VARCHAR(10) DEFAULT 'HIGH' CHECK (priority_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    is_voice_activated BOOLEAN DEFAULT FALSE,
    responded_by INTEGER REFERENCES users(id),
    response_time TIMESTAMP,
    resolved_at TIMESTAMP,
    ai_analysis JSONB, -- Store AI analysis results
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Emergency Contacts table
CREATE TABLE emergency_contacts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    contact_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    relationship VARCHAR(50),
    is_primary BOOLEAN DEFAULT FALSE,
    notify_on_sos BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Help Requests table (For senior citizens and community help)
CREATE TABLE help_requests (
    id SERIAL PRIMARY KEY,
    requester_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL, -- 'medical', 'household', 'transport', 'grocery', 'companion'
    urgency VARCHAR(20) DEFAULT 'MEDIUM' CHECK (urgency IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    required_skills INTEGER[], -- Array of skill IDs
    preferred_volunteer_gender VARCHAR(20),
    estimated_duration VARCHAR(50), -- '1-2 hours', '3-4 hours', 'Half day', etc.
    location_address TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    max_distance_km INTEGER DEFAULT 5,
    scheduled_date DATE,
    scheduled_time TIME,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_pattern VARCHAR(50), -- 'daily', 'weekly', 'monthly'
    compensation_offered DECIMAL(10, 2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    assigned_volunteer_id INTEGER REFERENCES volunteers(id),
    assigned_at TIMESTAMP,
    completed_at TIMESTAMP,
    ai_recommended_volunteers JSONB, -- Store AI recommendations
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Help Request Responses table (Volunteers applying for help requests)
CREATE TABLE help_request_responses (
    id SERIAL PRIMARY KEY,
    help_request_id INTEGER REFERENCES help_requests(id) ON DELETE CASCADE,
    volunteer_id INTEGER REFERENCES volunteers(id) ON DELETE CASCADE,
    response_message TEXT,
    availability_confirmed BOOLEAN DEFAULT TRUE,
    estimated_arrival_time INTERVAL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(help_request_id, volunteer_id)
);

-- 9. Abuse Reports table
CREATE TABLE abuse_reports (
    id SERIAL PRIMARY KEY,
    reporter_id INTEGER REFERENCES users(id) ON DELETE SET NULL, -- Nullable for anonymous reports
    report_type VARCHAR(30) NOT NULL CHECK (report_type IN ('DOMESTIC_VIOLENCE', 'HARASSMENT', 'TRAFFICKING', 'CHILD_ABUSE', 'ELDER_ABUSE', 'OTHER')),
    is_anonymous BOOLEAN DEFAULT FALSE,
    victim_name VARCHAR(100),
    victim_age INTEGER,
    victim_gender VARCHAR(20),
    incident_location TEXT,
    incident_date DATE,
    incident_time TIME,
    description TEXT NOT NULL,
    evidence_files TEXT[], -- Array of file URLs
    urgency_level VARCHAR(20) DEFAULT 'MEDIUM' CHECK (urgency_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    status VARCHAR(20) DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'INVESTIGATING', 'RESOLVED', 'CLOSED')),
    assigned_admin_id INTEGER REFERENCES users(id),
    police_report_number VARCHAR(100),
    follow_up_required BOOLEAN DEFAULT TRUE,
    ai_risk_assessment JSONB, -- AI analysis for risk level
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Missing Persons table
CREATE TABLE missing_persons (
    id SERIAL PRIMARY KEY,
    reporter_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    missing_person_name VARCHAR(100) NOT NULL,
    age INTEGER,
    gender VARCHAR(20),
    height VARCHAR(20),
    weight VARCHAR(20),
    hair_color VARCHAR(30),
    eye_color VARCHAR(30),
    distinctive_features TEXT,
    photo_url VARCHAR(255),
    last_seen_location TEXT,
    last_seen_latitude DECIMAL(10, 8),
    last_seen_longitude DECIMAL(11, 8),
    last_seen_date DATE NOT NULL,
    last_seen_time TIME,
    clothing_description TEXT,
    medical_conditions TEXT,
    contact_person_name VARCHAR(100),
    contact_person_phone VARCHAR(20),
    police_report_number VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'FOUND', 'CLOSED')),
    search_radius_km INTEGER DEFAULT 50,
    found_date TIMESTAMP,
    found_location TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Missing Person Sightings table
CREATE TABLE missing_person_sightings (
    id SERIAL PRIMARY KEY,
    missing_person_id INTEGER REFERENCES missing_persons(id) ON DELETE CASCADE,
    reporter_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    sighting_location TEXT NOT NULL,
    sighting_latitude DECIMAL(10, 8),
    sighting_longitude DECIMAL(11, 8),
    sighting_date DATE NOT NULL,
    sighting_time TIME,
    description TEXT,
    confidence_level VARCHAR(20) DEFAULT 'MEDIUM' CHECK (confidence_level IN ('LOW', 'MEDIUM', 'HIGH')),
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by INTEGER REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. Blood Donors table
CREATE TABLE blood_donors (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    blood_type VARCHAR(5) NOT NULL CHECK (blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    is_available BOOLEAN DEFAULT TRUE,
    last_donation_date DATE,
    medical_eligibility BOOLEAN DEFAULT TRUE,
    weight_kg INTEGER,
    health_conditions TEXT,
    preferred_donation_centers TEXT[],
    emergency_donor BOOLEAN DEFAULT FALSE, -- Willing to donate in emergencies
    contact_preference VARCHAR(20) DEFAULT 'PHONE' CHECK (contact_preference IN ('PHONE', 'EMAIL', 'APP')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 13. Blood Requests table
CREATE TABLE blood_requests (
    id SERIAL PRIMARY KEY,
    requester_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    patient_name VARCHAR(100) NOT NULL,
    blood_type VARCHAR(5) NOT NULL CHECK (blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    units_needed INTEGER DEFAULT 1,
    urgency VARCHAR(20) DEFAULT 'MEDIUM' CHECK (urgency IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    hospital_name VARCHAR(200),
    hospital_address TEXT,
    hospital_latitude DECIMAL(10, 8),
    hospital_longitude DECIMAL(11, 8),
    contact_person VARCHAR(100),
    contact_phone VARCHAR(20),
    needed_by_date DATE,
    needed_by_time TIME,
    additional_requirements TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PARTIALLY_FULFILLED', 'FULFILLED', 'EXPIRED')),
    fulfilled_units INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. Notifications table (System notifications)
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    recipient_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(30) NOT NULL CHECK (notification_type IN ('SOS_ALERT', 'HELP_REQUEST', 'BLOOD_REQUEST', 'MISSING_PERSON', 'SYSTEM', 'REMINDER')),
    related_id INTEGER, -- ID of related record (sos_alert_id, help_request_id, etc.)
    is_read BOOLEAN DEFAULT FALSE,
    priority VARCHAR(20) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    action_required BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(255),
    expires_at TIMESTAMP,
    sent_via VARCHAR(20) DEFAULT 'APP' CHECK (sent_via IN ('APP', 'EMAIL', 'SMS', 'PUSH')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert some basic skills
INSERT INTO skills (skill_name, description, category) VALUES
('Medical Assistance', 'Basic medical help and first aid', 'medical'),
('Transportation', 'Providing rides and transport services', 'transport'),
('Grocery Shopping', 'Shopping for groceries and essentials', 'household'),
('Household Chores', 'General household cleaning and maintenance', 'household'),
('Companionship', 'Providing emotional support and company', 'social'),
('Technology Help', 'Assistance with computers and smartphones', 'technical'),
('Medication Management', 'Help with medication schedules', 'medical'),
('Pet Care', 'Taking care of pets', 'household'),
('Language Translation', 'Translation and interpretation services', 'communication'),
('Legal Assistance', 'Basic legal guidance and support', 'professional');

-- Create indexes for better performance
CREATE INDEX idx_users_location ON users (latitude, longitude);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_phone ON users (phone);
CREATE INDEX idx_volunteers_user_id ON volunteers (user_id);
CREATE INDEX idx_sos_alerts_user_id ON sos_alerts (user_id);
CREATE INDEX idx_sos_alerts_location ON sos_alerts (latitude, longitude);
CREATE INDEX idx_sos_alerts_status ON sos_alerts (status);
CREATE INDEX idx_help_requests_status ON help_requests (status);
CREATE INDEX idx_help_requests_location ON help_requests (latitude, longitude);
CREATE INDEX idx_help_requests_category ON help_requests (category);
CREATE INDEX idx_missing_persons_status ON missing_persons (status);
CREATE INDEX idx_blood_requests_blood_type ON blood_requests (blood_type);
CREATE INDEX idx_blood_requests_status ON blood_requests (status);
CREATE INDEX idx_notifications_recipient ON notifications (recipient_id, is_read);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at columns
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_volunteers_updated_at BEFORE UPDATE ON volunteers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_help_requests_updated_at BEFORE UPDATE ON help_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_abuse_reports_updated_at BEFORE UPDATE ON abuse_reports FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_missing_persons_updated_at BEFORE UPDATE ON missing_persons FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_blood_donors_updated_at BEFORE UPDATE ON blood_donors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_blood_requests_updated_at BEFORE UPDATE ON blood_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

