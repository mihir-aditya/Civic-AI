<?php

namespace App\Services;

use Illuminate\Support\Facades\Storage;
use Illuminate\Http\UploadedFile;

class GcsStorageService
{
    /**
     * Upload an uploaded file to GCS or fallback local disk storage.
     */
    public function upload(UploadedFile $file, string $directory = 'hazards'): string
    {
        $filename = time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();
        
        // If Google Cloud Storage driver is configured in filesystems.php, upload there
        if (config('filesystems.disks.gcs.bucket')) {
            return Storage::disk('gcs')->putFileAs($directory, $file, $filename, 'public');
        }

        // Fallback to local public disk storage
        return Storage::disk('public')->putFileAs($directory, $file, $filename);
    }

    /**
     * Delete a file from GCS or local disk.
     */
    public function delete(string $path): bool
    {
        if (config('filesystems.disks.gcs.bucket')) {
            return Storage::disk('gcs')->delete($path);
        }

        return Storage::disk('public')->delete($path);
    }
}
