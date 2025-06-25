'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { toast } from 'sonner';
import { ArrowLeft, Building, MapPin, DollarSign, Upload, X, Image as ImageIcon, Loader2 } from 'lucide-react';
import Link from 'next/link';
import { ownerService } from '@/services/owner';
import { uploadService } from '@/services/upload';
import { useApiMutation, useApi } from '@/hooks/useApi';

const createRenthouseSchema = z.object({
  name: z.string().min(1, 'Property name is required').max(100, 'Name too long'),
  description: z.string().optional(),
  latitude: z.string().min(1, 'Latitude is required'),
  longitude: z.string().min(1, 'Longitude is required'),
  address: z.string().min(1, 'Address is required'),
  baseRent: z.string().min(1, 'Base rent is required').regex(/^\d+(\.\d{1,2})?$/, 'Invalid base rent format'),
  waterFee: z.string().min(1, 'Water fee is required').regex(/^\d+(\.\d{1,2})?$/, 'Invalid water fee format'),
  electricityFee: z.string().min(1, 'Electricity fee is required').regex(/^\d+(\.\d{1,2})?$/, 'Invalid electricity fee format'),
  qrCodeImage: z.string().max(2000, 'QR Code image URL too long').optional(),
  imageUrl: z.string().max(2000, 'Image URL too long').optional(),
});

type CreateRenthouseForm = z.infer<typeof createRenthouseSchema>;

// This type should match what the backend's CreateRenthouseRequest expects
type CreateRenthousePayload = Omit<CreateRenthouseForm, 'baseRent' | 'latitude' | 'longitude'> & {
  baseRent: number;
  latitude: number;
  longitude: number;
  address: string;
};

export default function NewRenthousePage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const editId = searchParams.get('edit');
  const isEditMode = !!editId;

  const createMutation = useApiMutation(ownerService.createRenthouse);
  const updateMutation = useApiMutation(ownerService.updateRenthouse);

  // Fetch existing property data if in edit mode
  const { data: existingProperty, loading: loadingProperty } = useApi(
    () => editId ? ownerService.getRenthouseById(parseInt(editId)) : Promise.resolve({ success: true, data: null, message: '' }),
    { autoFetch: !!editId }
  );

  const [useCurrentLocation, setUseCurrentLocation] = useState(false);
  const [propertyImagePreview, setPropertyImagePreview] = useState<string | null>(null);
  const [qrCodeImagePreview, setQrCodeImagePreview] = useState<string | null>(null);
  const [uploadingPropertyImage, setUploadingPropertyImage] = useState(false);
  const [uploadingQrImage, setUploadingQrImage] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    reset,
    formState: { errors },
  } = useForm<CreateRenthouseForm>({
    resolver: zodResolver(createRenthouseSchema),
  });

  const watchedLatitude = watch('latitude');
  const watchedLongitude = watch('longitude');

  // Populate form with existing data when in edit mode
  useEffect(() => {
    if (existingProperty && isEditMode) {
      const property = existingProperty;
      reset({
        name: property.name || '',
        description: property.description || '',
        latitude: property.latitude?.toString() || '',
        longitude: property.longitude?.toString() || '',
        address: property.address || '',
        baseRent: property.baseRent?.toString() || '',
        waterFee: property.waterFee?.toString() || '',
        electricityFee: property.electricityFee?.toString() || '',
        qrCodeImage: property.qrCodeImage || '',
        imageUrl: property.imageUrl || '',
      });

      // Set image previews if they exist
      if (property.imageUrl) {
        const apiBaseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
        setPropertyImagePreview(`${apiBaseUrl.replace('/api', '')}${property.imageUrl}`);
      }
      if (property.qrCodeImage) {
        const apiBaseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
        setQrCodeImagePreview(`${apiBaseUrl.replace('/api', '')}${property.qrCodeImage}`);
      }
    }
  }, [existingProperty, isEditMode, reset]);

  const onSubmit = async (data: CreateRenthouseForm) => {
    try {
      // Convert string values to appropriate types for the payload
      const payload: CreateRenthousePayload = {
        ...data,
        latitude: parseFloat(data.latitude),
        longitude: parseFloat(data.longitude),
        address: data.address,
        baseRent: parseFloat(data.baseRent),
      };

      if (isEditMode && editId) {
        await updateMutation.mutate(parseInt(editId), payload);
        toast.success('Property updated successfully!');
      } else {
        await createMutation.mutate(payload);
        toast.success('Property created successfully!');
      }

      router.push('/owner/renthouses');
    } catch (error) {
      console.error('Failed to save property:', error);
      toast.error(isEditMode ? 'Failed to update property' : 'Failed to create property');
    }
  };

  const getCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude.toString();
          const lng = position.coords.longitude.toString();

          setValue('latitude', lat);
          setValue('longitude', lng);
          setValue('address', ''); // Clear address when using coordinates
          setUseCurrentLocation(true);
          toast.success('Location detected successfully!');
        },
        (error) => {
          toast.error('Unable to get your location');
        }
      );
    } else {
      toast.error('Geolocation is not supported by this browser');
    }
  };

  const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>, type: 'property' | 'qr') => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      toast.error('Please select a valid image file');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      toast.error('Image size must be less than 5MB');
      return;
    }

    try {
      // Set loading state
      if (type === 'property') {
        setUploadingPropertyImage(true);
      } else {
        setUploadingQrImage(true);
      }

      // Upload file to server
      const response = await uploadService.uploadImage(file);

      if (response.success && response.data) {
        // Create preview URL using the API base URL
        const apiBaseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
        const previewUrl = `${apiBaseUrl.replace('/api', '')}${response.data}`;

        if (type === 'property') {
          setPropertyImagePreview(previewUrl);
          setValue('imageUrl', response.data);
        } else {
          setQrCodeImagePreview(previewUrl);
          setValue('qrCodeImage', response.data);
        }
        toast.success('Image uploaded successfully!');
      } else {
        toast.error(response.message || 'Failed to upload image');
      }
    } catch (error) {
      console.error('Upload error:', error);
      toast.error('Failed to upload image');
    } finally {
      // Clear loading state
      if (type === 'property') {
        setUploadingPropertyImage(false);
      } else {
        setUploadingQrImage(false);
      }
    }
  };

  const removeImage = (type: 'property' | 'qr') => {
    if (type === 'property') {
      setPropertyImagePreview(null);
      setValue('imageUrl', '');
    } else {
      setQrCodeImagePreview(null);
      setValue('qrCodeImage', '');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center">
        <Link href="/owner/renthouses">
          <Button variant="ghost" size="sm">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Properties
          </Button>
        </Link>
      </div>

      <div>
        <h1 className="text-3xl font-bold tracking-tight">
          {isEditMode ? 'Edit Property' : 'Add New Property'}
        </h1>
        <p className="text-muted-foreground">
          {isEditMode ? 'Update your rental property information' : 'Create a new rental property to manage'}
        </p>
      </div>

      {/* Show loading state when fetching existing property data */}
      {isEditMode && loadingProperty ? (
        <Card className="max-w-7xl">
          <CardContent className="flex justify-center items-center py-12">
            <Loader2 className="h-8 w-8 animate-spin" />
            <span className="ml-2">Loading property data...</span>
          </CardContent>
        </Card>
      ) : (
        <Card className="max-w-7xl">
          <CardHeader>
            <CardTitle className="flex items-center">
              <Building className="mr-2 h-5 w-5" />
              Property Information
            </CardTitle>
            <CardDescription>
              {isEditMode ? 'Update the details for your rental property' : 'Fill in the details for your new rental property'}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              {/* Basic Information */}
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Property Name</Label>
                  <Input
                    id="name"
                    placeholder="e.g., Sunny Apartments"
                    className={errors.name ? 'border-red-500' : ''}
                    {...register('name')}
                    disabled={createMutation.loading || updateMutation.loading}
                  />
                  {errors.name && (
                    <p className="text-sm text-red-600">{errors.name.message}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="description">Description (Optional)</Label>
                  <Textarea
                    id="description"
                    placeholder="Describe your property..."
                    rows={3}
                    className={errors.description ? 'border-red-500' : ''}
                    {...register('description')}
                    disabled={createMutation.loading || updateMutation.loading}
                  />
                </div>
              </div>

              {/* Location */}
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <MapPin className="mr-2 h-5 w-5" />
                    <Label className="text-base font-medium">Location</Label>
                  </div>
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={getCurrentLocation}
                    disabled={createMutation.loading || updateMutation.loading}
                  >
                    <MapPin className="mr-2 h-4 w-4" />
                    Use Current Location
                  </Button>
                </div>

                <div className="grid grid-cols-3 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="latitude">Latitude</Label>
                    <Input
                      id="latitude"
                      placeholder="e.g., 40.7128"
                      className={errors.latitude ? 'border-red-500' : ''}
                      {...register('latitude')}
                      disabled={createMutation.loading || updateMutation.loading}
                    />
                    {errors.latitude && (
                      <p className="text-sm text-red-600">{errors.latitude.message}</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="longitude">Longitude</Label>
                    <Input
                      id="longitude"
                      placeholder="e.g., -74.0060"
                      className={errors.longitude ? 'border-red-500' : ''}
                      {...register('longitude')}
                      disabled={createMutation.loading || updateMutation.loading}
                    />
                    {errors.longitude && (
                      <p className="text-sm text-red-600">{errors.longitude.message}</p>
                    )}
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="address">Address</Label>
                    <Input
                      id="address"
                      placeholder="e.g., 123 Main Street, City, State"
                      className={errors.address ? 'border-red-500' : ''}
                      {...register('address')}
                      disabled={createMutation.loading || updateMutation.loading}
                    />
                    {errors.address && (
                      <p className="text-sm text-red-600">{errors.address.message}</p>
                    )}
                  </div>
                </div>
              </div>

              {/* Financial Information */}
              <div className="space-y-4">
                <div className='grid grid-cols-3 gap-4'>
                  <div className="space-y-2">
                    <Label htmlFor="waterFee">Water Fee</Label>
                    <div className="relative">
                      <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                      <Input
                        id="waterFee"
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        className={`pl-10 ${errors.waterFee ? 'border-red-500' : ''}`}
                        {...register('waterFee')}
                        disabled={createMutation.loading || updateMutation.loading}
                      />
                    </div>
                    {errors.waterFee && (
                      <p className="text-sm text-red-600">{errors.waterFee.message}</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="electricityFee">Electricity Fee</Label>
                    <div className="relative">
                      <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                      <Input
                        id="electricityFee"
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        className={`pl-10 ${errors.electricityFee ? 'border-red-500' : ''}`}
                        {...register('electricityFee')}
                        disabled={createMutation.loading || updateMutation.loading}
                      />
                    </div>
                    {errors.electricityFee && (
                      <p className="text-sm text-red-600">{errors.electricityFee.message}</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="baseRent">Base Rent</Label>
                    <div className="relative">
                      <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                      <Input
                        id="baseRent"
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        className={`pl-10 ${errors.baseRent ? 'border-red-500' : ''}`}
                        {...register('baseRent')}
                        disabled={createMutation.loading || updateMutation.loading}
                      />
                    </div>
                    {errors.baseRent && (
                      <p className="text-sm text-red-600">{errors.baseRent.message}</p>
                    )}
                  </div>
                </div>
              </div>

              {/* Images */}
              <div className="space-y-6">
                {/* Property Image */}
                <div className="space-y-4">
                  <Label>Property Image (Optional)</Label>
                  <div className="space-y-3">
                    {propertyImagePreview ? (
                      <div className="relative">
                        <img
                          src={propertyImagePreview}
                          alt="Property preview"
                          className="w-full h-48 object-cover rounded-lg border"
                        />
                        <Button
                          type="button"
                          variant="destructive"
                          size="sm"
                          className="absolute top-2 right-2"
                          onClick={() => removeImage('property')}
                          disabled={uploadingPropertyImage || createMutation.loading || updateMutation.loading}
                        >
                          <X className="h-4 w-4" />
                        </Button>
                      </div>
                    ) : (
                      <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                        <ImageIcon className="mx-auto h-12 w-12 text-gray-400" />
                        <div className="mt-2">
                          <Button
                            type="button"
                            variant="outline"
                            className="mt-2"
                            onClick={() => document.getElementById('property-image-upload')?.click()}
                            disabled={uploadingPropertyImage || createMutation.loading || updateMutation.loading}
                          >
                            <Upload className="mr-2 h-4 w-4" />
                            {uploadingPropertyImage ? 'Uploading...' : 'Upload Property Image'}
                          </Button>
                        </div>
                        <p className="text-sm text-gray-500 mt-2">
                          PNG, JPG, GIF up to 5MB
                        </p>
                      </div>
                    )}
                    <input
                      id="property-image-upload"
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={(e) => handleImageUpload(e, 'property')}
                      disabled={uploadingPropertyImage || createMutation.loading || updateMutation.loading}
                    />
                  </div>
                </div>

                {/* QR Code Image */}
                <div className="space-y-4">
                  <Label>QR Code Image (Optional)</Label>
                  <div className="space-y-3">
                    {qrCodeImagePreview ? (
                      <div className="relative">
                        <img
                          src={qrCodeImagePreview}
                          alt="QR Code preview"
                          className="w-full h-48 object-cover rounded-lg border"
                        />
                        <Button
                          type="button"
                          variant="destructive"
                          size="sm"
                          className="absolute top-2 right-2"
                          onClick={() => removeImage('qr')}
                          disabled={uploadingQrImage || createMutation.loading || updateMutation.loading}
                        >
                          <X className="h-4 w-4" />
                        </Button>
                      </div>
                    ) : (
                      <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                        <ImageIcon className="mx-auto h-12 w-12 text-gray-400" />
                        <div className="mt-2">
                          <Button
                            type="button"
                            variant="outline"
                            className="mt-2"
                            onClick={() => document.getElementById('qr-image-upload')?.click()}
                            disabled={uploadingQrImage || createMutation.loading || updateMutation.loading}
                          >
                            <Upload className="mr-2 h-4 w-4" />
                            {uploadingQrImage ? 'Uploading...' : 'Upload QR Code Image'}
                          </Button>
                        </div>
                        <p className="text-sm text-gray-500 mt-2">
                          PNG, JPG, GIF up to 5MB
                        </p>
                      </div>
                    )}
                    <input
                      id="qr-image-upload"
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={(e) => handleImageUpload(e, 'qr')}
                      disabled={uploadingQrImage || createMutation.loading || updateMutation.loading}
                    />
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="flex justify-end space-x-4 pt-6">
                <Link href="/owner/renthouses">
                  <Button variant="outline" disabled={createMutation.loading || updateMutation.loading}>
                    Cancel
                  </Button>
                </Link>
                <Button type="submit" disabled={createMutation.loading || updateMutation.loading}>
                  {createMutation.loading || updateMutation.loading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      {isEditMode ? 'Updating...' : 'Creating...'}
                    </>
                  ) : (
                    isEditMode ? 'Update Property' : 'Create Property'
                  )}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}
    </div>
  );
}