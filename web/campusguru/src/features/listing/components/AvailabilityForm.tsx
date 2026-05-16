import { useState } from 'react';
import type { FormEvent } from 'react';
import Button from '@shared/components/Button';
import { listingService } from '../services/listingService';

interface AvailabilityFormProps {
  listingId: number;
  currentAvailability?: {
    availabilityStartDate?: string | null;
    availabilityEndDate?: string | null;
    availabilityDailyStartTime?: string | null;
    availabilityDailyEndTime?: string | null;
  };
  onSaved?: () => void;
}

const AvailabilityForm = ({ listingId, currentAvailability, onSaved }: AvailabilityFormProps) => {
  const [startDate, setStartDate] = useState(currentAvailability?.availabilityStartDate || '');
  const [endDate, setEndDate] = useState(currentAvailability?.availabilityEndDate || '');
  const [dailyStart, setDailyStart] = useState(currentAvailability?.availabilityDailyStartTime || '09:00');
  const [dailyEnd, setDailyEnd] = useState(currentAvailability?.availabilityDailyEndTime || '17:00');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!startDate || !endDate || !dailyStart || !dailyEnd) {
      setError('All fields are required.');
      return;
    }

    setSaving(true);
    try {
      await listingService.setAvailability(listingId, {
        availabilityStartDate: startDate,
        availabilityEndDate: endDate,
        availabilityDailyStartTime: dailyStart,
        availabilityDailyEndTime: dailyEnd,
      });
      setSuccess('Availability saved! A calendar event has been created.');
      onSaved?.();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to save availability.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="rounded-xl border border-slate-200 bg-slate-50 p-5">
      <h3 className="text-lg font-bold text-slate-900">Set Availability Window</h3>
      <p className="mt-1 text-sm text-slate-500">Define when learners can book sessions with you.</p>

      {error && <p className="mt-3 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}
      {success && <p className="mt-3 rounded-md bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{success}</p>}

      <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1.5 block text-xs font-semibold text-gray-800">Start Date</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />
        </div>
        <div>
          <label className="mb-1.5 block text-xs font-semibold text-gray-800">End Date</label>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />
        </div>
        <div>
          <label className="mb-1.5 block text-xs font-semibold text-gray-800">Daily Start Time</label>
          <input
            type="time"
            value={dailyStart}
            onChange={(e) => setDailyStart(e.target.value)}
            className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />
        </div>
        <div>
          <label className="mb-1.5 block text-xs font-semibold text-gray-800">Daily End Time</label>
          <input
            type="time"
            value={dailyEnd}
            onChange={(e) => setDailyEnd(e.target.value)}
            className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />
        </div>
      </div>

      <div className="mt-4">
        <Button type="submit" disabled={saving}>
          {saving ? 'Saving...' : 'Save Availability'}
        </Button>
      </div>
    </form>
  );
};

export default AvailabilityForm;
