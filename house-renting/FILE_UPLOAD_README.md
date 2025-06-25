# File Upload API Documentation

## Overview
The file upload API allows users to upload images for property photos and QR codes. Files are stored locally on the server and served via HTTP endpoints.

## Endpoints

### Upload Image
- **URL**: `POST /api/upload/image`
- **Authentication**: Required (Bearer token)
- **Authorization**: OWNER or USER role
- **Content-Type**: `multipart/form-data`

#### Request Parameters
- `file`: The image file to upload (required)

#### File Requirements
- **File Type**: Only image files (JPEG, PNG, GIF, WebP)
- **File Size**: Maximum 5MB
- **Supported Formats**: jpg, jpeg, png, gif, webp

#### Response
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": "/api/upload/files/uuid-filename.jpg"
}
```

### Get Uploaded File
- **URL**: `GET /api/upload/files/{filename}`
- **Authentication**: Not required (public access)
- **Response**: The image file with appropriate Content-Type header

## Configuration

### Application Properties
```properties
# File Upload Configuration
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
file.upload.path=uploads
file.upload.max-size=5242880
```

### Security
- Upload endpoints require authentication
- File serving endpoints are public
- CORS is configured to allow cross-origin requests

## Usage in Frontend

### Upload Service
```typescript
import { uploadService } from '@/services/upload';

// Upload an image
const response = await uploadService.uploadImage(file);
if (response.success) {
  const imageUrl = response.data; // Returns the file path
  const fullUrl = `${API_BASE_URL.replace('/api', '')}${imageUrl}`;
}
```

### Integration with Property Creation
When creating a property, the uploaded file URLs are stored in:
- `imageUrl`: Property image URL
- `qrCodeImage`: QR code image URL

## File Storage
- Files are stored in the `uploads/` directory (configurable)
- Unique filenames are generated using UUID
- Original file extensions are preserved
- Directory is created automatically if it doesn't exist

## Error Handling
- File size validation
- File type validation
- File existence checks
- Proper error messages returned to client

## Security Considerations
- Only authenticated users can upload files
- File type validation prevents malicious uploads
- File size limits prevent abuse
- Unique filenames prevent conflicts 