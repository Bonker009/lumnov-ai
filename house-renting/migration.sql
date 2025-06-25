-- Migration script to add new fields to renthouses table
-- Run this script on your PostgreSQL database

-- Make existing location columns nullable
ALTER TABLE renthouses ALTER COLUMN address DROP NOT NULL;
ALTER TABLE renthouses ALTER COLUMN latitude DROP NOT NULL;
ALTER TABLE renthouses ALTER COLUMN longitude DROP NOT NULL;

-- Add new columns
ALTER TABLE renthouses ADD COLUMN water_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00;
ALTER TABLE renthouses ADD COLUMN electricity_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00;
ALTER TABLE renthouses ADD COLUMN qr_code_image VARCHAR(255);

-- Update image columns to TEXT to handle long URLs
ALTER TABLE renthouses ALTER COLUMN image_url TYPE TEXT;
ALTER TABLE renthouses ALTER COLUMN qr_code_image TYPE TEXT;

-- Add comments for documentation
COMMENT ON COLUMN renthouses.water_fee IS 'Water fee amount for the property';
COMMENT ON COLUMN renthouses.electricity_fee IS 'Electricity fee amount for the property';
COMMENT ON COLUMN renthouses.qr_code_image IS 'URL to QR code image for the property';
COMMENT ON COLUMN renthouses.image_url IS 'URL to property image'; 