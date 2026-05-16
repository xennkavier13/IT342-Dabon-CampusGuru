import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import MarketplaceNavbar from '@shared/components/MarketplaceNavbar';
import { listingService } from '../services/listingService';
import { SlotPicker } from '@features/booking';
import type { Listing } from '../types/listing.types';

const LearnerListingDetailsPage = () => {
  const { id } = useParams();
  const listingId = Number(id);

  const [listing, setListing] = useState<Listing | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');



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
        const data = await listingService.getListingById(listingId);
        setListing(data);
      } catch (requestError: any) {
        setError(requestError?.response?.data?.message || 'Failed to load listing details.');
      } finally {
        setLoading(false);
      }
    };

    void fetchListing();
  }, [listingId]);


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
              <p><span className="font-semibold">Contact Info:</span> {listing.contactInfo}</p>
              <p>
                <span className="font-semibold">Proof of Competence:</span>{' '}
                <a href={listing.proofOfCompetenceUrl} target="_blank" rel="noreferrer" className="font-semibold text-blue-600 hover:text-blue-700">
                  View Document
                </a>
              </p>
              <p>
                <span className="font-semibold">Scheduling Window:</span>{' '}
                {listing.availabilityStartDate ? (
                  <span>
                    {listing.availabilityStartDate} to {listing.availabilityEndDate} · {listing.availabilityDailyStartTime} – {listing.availabilityDailyEndTime}
                  </span>
                ) : (
                  <span className="italic text-slate-500">Not set by tutor</span>
                )}
              </p>
            </div>

            {message && <p className="mt-4 rounded-md bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p>}

            {listing.availabilityStartDate ? (
              <SlotPicker listingId={listing.id} onBooked={() => setMessage('Booking request submitted!')} />
            ) : (
              <div className="mt-6 rounded-xl border border-slate-200 bg-slate-50 p-6 text-center text-sm text-slate-500">
                This tutor hasn't set their scheduling availability yet. Please check back later.
              </div>
            )}
          </section>
        )}
      </main>
    </div>
  );
};

export default LearnerListingDetailsPage;
