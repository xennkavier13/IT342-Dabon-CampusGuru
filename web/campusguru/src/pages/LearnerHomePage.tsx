import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import MarketplaceNavbar from '../components/MarketplaceNavbar';
import { marketplaceService } from '../services/marketplaceService';
import type { Listing } from '../types/marketplace.types';

const LearnerHomePage = () => {
  const [listings, setListings] = useState<Listing[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchListings = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await marketplaceService.getListings();
        setListings(data);
      } catch (requestError: any) {
        setError(requestError?.response?.data?.message || 'Failed to fetch tutor listings.');
      } finally {
        setLoading(false);
      }
    };

    void fetchListings();
  }, []);

  return (
    <div className="min-h-screen bg-slate-50">
      <MarketplaceNavbar title="Find a Tutor" />

      <main className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
        <section>
          <h2 className="text-2xl font-bold text-slate-900">Recently Posted Listings</h2>
          <p className="mt-1 text-sm text-slate-600">Discover tutors and book the session that fits your schedule.</p>

          {error && <p className="mt-4 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}

          {loading ? (
            <p className="mt-6 text-sm text-slate-600">Loading listings...</p>
          ) : (
            <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {listings.length === 0 && (
                <div className="rounded-xl border border-slate-200 bg-white p-6 text-sm text-slate-500">
                  No listings yet. Check back soon.
                </div>
              )}

              {listings.map((listing) => (
                <article key={listing.id} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
                  <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">{listing.tutorName}</p>
                  <h3 className="mt-2 text-xl font-bold text-slate-900">{listing.subject}</h3>
                  <p className="mt-2 text-sm text-slate-600">{listing.availableTime}</p>
                  <p className="mt-2 text-sm text-slate-600">Asking Price: Free</p>

                  <Link
                    to={`/learner/listings/${listing.id}`}
                    className="mt-4 inline-flex rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
                  >
                    View Listing
                  </Link>
                </article>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default LearnerHomePage;
