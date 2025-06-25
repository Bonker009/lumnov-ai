# Renthouse Form Updates - Migration Guide

## Overview
This update modifies the renthouse creation form to:
1. Remove the standalone address field
2. Add conditional location input (coordinates OR address)
3. Add three new required fields: water fee, electricity fee, and QR code image
4. Improve form validation

## Database Changes

### New Columns Added
- `water_fee` (DECIMAL(10,2), NOT NULL, DEFAULT 0.00)
- `electricity_fee` (DECIMAL(10,2), NOT NULL, DEFAULT 0.00)  
- `qr_code_image` (VARCHAR(255), NULLABLE)

### Modified Columns
- `address` - Made nullable (was NOT NULL)
- `latitude` - Made nullable (was NOT NULL)
- `longitude` - Made nullable (was NOT NULL)

## Migration Steps

1. **Run the migration script:**
   ```sql
   -- Execute the migration.sql file in your PostgreSQL database
   psql -d lumnov -f migration.sql
   ```

2. **Restart the Spring Boot application:**
   The application will automatically detect the schema changes.

## Frontend Changes

### Form Behavior
- **Location Input**: Users can now choose between:
  - "Use Current Location" - Automatically fills latitude/longitude
  - "Use Address" - Shows address input field
- **Required Fields**: Water fee and electricity fee are now required
- **Validation**: Proper validation for all new fields

### New Fields
1. **Water Fee**: Required numeric field with decimal support
2. **Electricity Fee**: Required numeric field with decimal support  
3. **QR Code Image**: Optional URL field for QR code image

## API Changes

### CreateRenthouseRequest
- `address`, `latitude`, `longitude` are now optional
- `waterFee` (string) - Required
- `electricityFee` (string) - Required
- `qrCodeImage` (string) - Optional

### RenthouseDto
- Added `waterFee`, `electricityFee`, `qrCodeImage` fields
- Made location fields optional

## Validation Rules

### Frontend Validation (Zod Schema)
- Water fee: Required, must match pattern `/^\d+(\.\d{1,2})?$/`
- Electricity fee: Required, must match pattern `/^\d+(\.\d{1,2})?$/`
- QR code image: Optional, must be valid URL if provided
- Location: Either address OR (latitude AND longitude) must be provided

### Backend Validation
- Water fee: Required, pattern validation for decimal format
- Electricity fee: Required, pattern validation for decimal format
- Location fields: Made optional

## Testing

1. **Test location functionality:**
   - Try "Use Current Location" button
   - Try "Use Address" button
   - Verify form validation works correctly

2. **Test new required fields:**
   - Verify water fee and electricity fee are required
   - Test decimal input validation
   - Test QR code image URL validation

3. **Test form submission:**
   - Verify data is saved correctly to database
   - Check that all new fields are persisted

## Rollback Plan

If you need to rollback these changes:

1. **Database rollback:**
   ```sql
   -- Remove new columns
   ALTER TABLE renthouses DROP COLUMN water_fee;
   ALTER TABLE renthouses DROP COLUMN electricity_fee;
   ALTER TABLE renthouses DROP COLUMN qr_code_image;
   
   -- Restore NOT NULL constraints
   ALTER TABLE renthouses ALTER COLUMN address SET NOT NULL;
   ALTER TABLE renthouses ALTER COLUMN latitude SET NOT NULL;
   ALTER TABLE renthouses ALTER COLUMN longitude SET NOT NULL;
   ```

2. **Revert code changes** to previous versions of:
   - `CreateRenthouseRequest.java`
   - `Renthouse.java`
   - `RenthouseDto.java`
   - `OwnerService.java`
   - Frontend form component
   - TypeScript type definitions 