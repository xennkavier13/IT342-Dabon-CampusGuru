import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { useParams } from 'react-router-dom';
import MarketplaceNavbar from '../components/MarketplaceNavbar';
import Button from '../components/Button';
import Input from '../components/Input';
import { marketplaceService } from '../services/marketplaceService';
import type { Listing } from '../types/marketplace.types';

const LearnerListingDetailsPage = () => {
  const { id } = useParams();
  const listingId = Number(id);

  const [listing, setListing] = useState<Listing | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const [requestedTime, setRequestedTime] = useState('');
  const [paymentType, setPaymentType] = useState('Cash');

  useEffect(() => {
    const fetchListing = async () => {
      if (!listingId) {
        setError('Invalid listing ID.');
        setLoading(false);
        return;
      }

      setLoading(true);
      setError('');
      try {
        const data = await marketplaceService.getListingById(listingId);
        setListing(data);
      } catch (requestError: any) {
        setError(requestError?.response?.data?.message || 'Failed to load listing details.');
      } finally {
        setLoading(false);
      }
    };

    void fetchListing();
  }, [listingId]);

  const handleBook = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setMessage('');

    if (!listing || !requestedTime.trim() || !paymentType.trim()) {
      setError('Please choose requested time and payment type.');
      return;
    }

    try {
      await marketplaceService.createBooking({
        listingId: listing.id,
        requestedTime,
        paymentType,
      });
      setMessage('Booking request submitted. Wait for tutor response.');
      setRequestedTime('');
    } catch (requestError: any) {
      setError(requestError?.response?.data?.message || 'Failed to create booking request.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <MarketplaceNavbar title="Listing Details" />

      <main className="mx-auto w-full max-w-4xl px-4 py-8 sm:px-6 lg:px-8">
        {loading && <p className="text-sm text-slate-600">Loading listing...</p>}

        {!loading && error && <p className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}

        {!loading && listing && (
          <section className="rounded-2xl border border-blue-100 bg-white p-6 shadow-sm">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">Tutor: {listing.tutorName}</p>
            <h2 className="mt-2 text-3xl font-bold text-slate-900">{listing.subject}</h2>

            <div className="mt-4 space-y-2 text-sm text-slate-700">
              <p><span className="font-semibold">Available Time:</span> {listing.availableTime}</p>
              <p><span className="font-semibold">Contact Info:</span> {listing.contactInfo}</p>
              <p>
                <span className="font-semibold">Proof of Competence:</span>{' '}
                <a href={listing.proofOfCompetenceUrl} target="_blank" rel="noreferrer" className="font-semibold text-blue-600 hover:text-blue-700">
                  View Document
                </a>
              </p>
            </div>

            {message && <p className="mt-4 rounded-md bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p>}

            <form onSubmit={handleBook} className="mt-6 rounded-xl border border-slate-200 bg-slate-50 p-4">
              <h3 className="text-lg font-bold text-slate-900">Book This Tutor</h3>

              <Input
                label="Requested Time"
                value={requestedTime}
                onChange={(event) => setRequestedTime(event.target.value)}
                placeholder="Tue 4:00 PM - 5:00 PM"
              />

              <div className="mb-4">
                <label className="mb-1.5 block text-xs font-semibold text-gray-800">Payment Type</label>
                <select
                  value={paymentType}
                  onChange={(event) => setPaymentType(event.target.value)}
                  className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                >
                  <option value="Cash">Cash</option>
                  <option value="GCash">GCash</option>
                </select>
              </div>

              <Button type="submit">Book this tutor</Button>
            </form>
          </section>
        )}
      </main>
    </div>
  );
};

export default LearnerListingDetailsPage;
