import { useEffect, useState } from 'react';
import { listingService } from '@features/listing/services/listingService';
import { bookingService } from '../services/bookingService';
import Button from '@shared/components/Button';
import type { AvailableSlot } from '@features/listing/types/listing.types';

interface SlotPickerProps {
  listingId: number;
  onBooked?: () => void;
}

const SlotPicker = ({ listingId, onBooked }: SlotPickerProps) => {
  const [slots, setSlots] = useState<AvailableSlot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedSlot, setSelectedSlot] = useState<AvailableSlot | null>(null);
  const [paymentType, setPaymentType] = useState('Cash');
  const [booking, setBooking] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchSlots = async () => {
      setLoading(true);
      try {
        const data = await listingService.getAvailableSlots(listingId);
        setSlots(data);
      } catch (err: any) {
        setError(err?.response?.data?.message || 'Failed to load available slots.');
      } finally {
        setLoading(false);
      }
    };
    void fetchSlots();
  }, [listingId]);

  const handleBook = async () => {
    if (!selectedSlot) return;
    setBooking(true);
    setError('');
    setMessage('');

    const start = new Date(selectedSlot.start);
    const requestedTime = start.toLocaleString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    });

    try {
      await bookingService.createBooking({
        listingId,
        requestedTime,
        paymentType,
        bookedStart: selectedSlot.start,
        bookedEnd: selectedSlot.end,
      });
      setMessage('Booking request submitted! Waiting for tutor approval.');
      setSelectedSlot(null);
      onBooked?.();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to create booking.');
    } finally {
      setBooking(false);
    }
  };

  // Group slots by date for display
  const slotsByDate = slots.reduce<Record<string, AvailableSlot[]>>((acc, slot) => {
    const dateKey = new Date(slot.start).toLocaleDateString('en-US', {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
    });
    if (!acc[dateKey]) acc[dateKey] = [];
    acc[dateKey].push(slot);
    return acc;
  }, {});

  if (loading) {
    return (
      <div className="mt-4 flex items-center gap-2 text-sm text-slate-500">
        <div className="h-4 w-4 animate-spin rounded-full border-2 border-blue-500 border-t-transparent" />
        Loading available slots...
      </div>
    );
  }

  return (
    <div className="mt-6 rounded-xl border border-slate-200 bg-slate-50 p-4">
      <h3 className="text-lg font-bold text-slate-900">Book a Session</h3>
      <p className="mt-1 text-sm text-slate-500">Select an available time slot below.</p>

      {error && <p className="mt-3 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}
      {message && <p className="mt-3 rounded-md bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{message}</p>}

      {Object.keys(slotsByDate).length === 0 ? (
        <p className="mt-4 text-sm text-slate-500">No available slots at the moment.</p>
      ) : (
        <div className="mt-4 space-y-4">
          {Object.entries(slotsByDate).map(([date, dateSlots]) => (
            <div key={date}>
              <p className="mb-2 text-xs font-semibold uppercase tracking-wider text-slate-600">{date}</p>
              <div className="flex flex-wrap gap-2">
                {dateSlots.map((slot) => {
                  const startTime = new Date(slot.start).toLocaleTimeString('en-US', {
                    hour: 'numeric',
                    minute: '2-digit',
                  });
                  const endTime = new Date(slot.end).toLocaleTimeString('en-US', {
                    hour: 'numeric',
                    minute: '2-digit',
                  });
                  const isSelected = selectedSlot?.start === slot.start;

                  return (
                    <button
                      key={slot.start}
                      type="button"
                      onClick={() => setSelectedSlot(isSelected ? null : slot)}
                      className={`rounded-lg border px-3 py-2 text-sm font-medium transition ${
                        isSelected
                          ? 'border-blue-500 bg-blue-50 text-blue-700 ring-2 ring-blue-200'
                          : 'border-slate-200 bg-white text-slate-700 hover:border-blue-300 hover:bg-blue-50'
                      }`}
                    >
                      {startTime} – {endTime}
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedSlot && (
        <div className="mt-4 space-y-3 rounded-lg border border-blue-100 bg-white p-4">
          <p className="text-sm font-medium text-slate-700">
            Selected:{' '}
            <span className="font-semibold text-blue-600">
              {new Date(selectedSlot.start).toLocaleString('en-US', {
                weekday: 'short',
                month: 'short',
                day: 'numeric',
                hour: 'numeric',
                minute: '2-digit',
              })}
            </span>
          </p>

          <div>
            <label className="mb-1.5 block text-xs font-semibold text-gray-800">Payment Type</label>
            <select
              value={paymentType}
              onChange={(e) => setPaymentType(e.target.value)}
              className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            >
              <option value="Cash">Cash</option>
              <option value="GCash">GCash</option>
            </select>
          </div>

          <Button onClick={() => void handleBook()} disabled={booking}>
            {booking ? 'Booking...' : 'Confirm Booking'}
          </Button>
        </div>
      )}
    </div>
  );
};

export default SlotPicker;
