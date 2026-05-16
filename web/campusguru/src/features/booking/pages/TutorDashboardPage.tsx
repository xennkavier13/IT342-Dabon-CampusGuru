import { useEffect, useMemo, useState } from 'react';
import MarketplaceNavbar from '@shared/components/MarketplaceNavbar';
import Button from '@shared/components/Button';
import Input from '@shared/components/Input';
import { bookingService } from '../services/bookingService';
import { listingService } from '@features/listing/services/listingService';
import { GoogleCalendarConnect } from '@features/googleauth';
import AvailabilityForm from '@features/listing/components/AvailabilityForm';
import type { Booking } from '../types/booking.types';
import type { Listing } from '@features/listing/types/listing.types';
import { useAuth } from '@features/auth';

const TutorDashboardPage = () => {
  const { user } = useAuth();
  const [pendingBookings, setPendingBookings] = useState<Booking[]>([]);
  const [listings, setListings] = useState<Listing[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [acceptingBooking, setAcceptingBooking] = useState<Booking | null>(null);
  const [decliningBooking, setDecliningBooking] = useState<Booking | null>(null);
  const [meetingLink, setMeetingLink] = useState('');
  const [declineReason, setDeclineReason] = useState('');
  const [selectedListingForAvailability, setSelectedListingForAvailability] = useState<Listing | null>(null);

  const isModalOpen = useMemo(() => !!acceptingBooking || !!decliningBooking, [acceptingBooking, decliningBooking]);

  const loadPendingBookings = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await bookingService.getPendingBookings();
      setPendingBookings(data);
    } catch (requestError: any) {
      setError(requestError?.response?.data?.message || 'Failed to fetch pending requests.');
    } finally {
      setLoading(false);
    }
  };

  const loadListings = async () => {
    try {
      const allListings = await listingService.getListings();
      // Filter to only show the current tutor's listings
      const myListings = allListings.filter(l => l.tutorId === user?.userId);
      setListings(myListings);
    } catch {
      // Silently fail — listings section is supplementary
    }
  };

  useEffect(() => {
    void loadPendingBookings();
    void loadListings();
  }, []);

  const handleAccept = async () => {
    if (!acceptingBooking || !meetingLink.trim()) {
      return;
    }

    await bookingService.updateBookingStatus(acceptingBooking.id, {
      status: 'ACCEPTED',
      meeting_link: meetingLink,
    });

    setAcceptingBooking(null);
    setMeetingLink('');
    await loadPendingBookings();
  };

  const handleDecline = async () => {
    if (!decliningBooking || !declineReason.trim()) {
      return;
    }

    await bookingService.updateBookingStatus(decliningBooking.id, {
      status: 'DECLINED',
      decline_reason: declineReason,
    });

    setDecliningBooking(null);
    setDeclineReason('');
    await loadPendingBookings();
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <MarketplaceNavbar title="Tutor Dashboard" />

      <main className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 lg:px-8 space-y-6">
        {/* Google Calendar Connection */}
        <section>
          <GoogleCalendarConnect />
        </section>

        {/* Availability Management */}
        {listings.length > 0 && (
          <section className="rounded-2xl border border-blue-100 bg-white p-6 shadow-sm">
            <h2 className="text-2xl font-bold text-slate-900">Manage Availability</h2>
            <p className="mt-1 text-sm text-slate-600">Set your availability window for each listing.</p>

            <div className="mt-4 space-y-3">
              {listings.map((listing) => (
                <div key={listing.id} className="flex items-center justify-between rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <div>
                    <p className="font-semibold text-slate-800">{listing.subject}</p>
                    {listing.availabilityStartDate ? (
                      <p className="text-xs text-slate-500">
                        {listing.availabilityStartDate} to {listing.availabilityEndDate} · {listing.availabilityDailyStartTime} – {listing.availabilityDailyEndTime}
                      </p>
                    ) : (
                      <p className="text-xs text-amber-600">No availability set</p>
                    )}
                  </div>
                  <Button
                    variant="outline"
                    onClick={() => setSelectedListingForAvailability(
                      selectedListingForAvailability?.id === listing.id ? null : listing
                    )}
                  >
                    {selectedListingForAvailability?.id === listing.id ? 'Close' : 'Set Availability'}
                  </Button>
                </div>
              ))}
            </div>

            {selectedListingForAvailability && (
              <div className="mt-4">
                <AvailabilityForm
                  listingId={selectedListingForAvailability.id}
                  currentAvailability={selectedListingForAvailability}
                  onSaved={() => {
                    setSelectedListingForAvailability(null);
                    void loadListings();
                  }}
                />
              </div>
            )}
          </section>
        )}

        {/* Pending Requests */}
        <section className="rounded-2xl border border-blue-100 bg-white p-6 shadow-sm">
          <h2 className="text-2xl font-bold text-slate-900">Pending Requests</h2>
          <p className="mt-1 text-sm text-slate-600">Review and respond to incoming booking requests from learners.</p>

          {error && <p className="mt-4 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}

          {loading ? (
            <p className="mt-6 text-sm text-slate-600">Loading requests...</p>
          ) : (
            <div className="mt-6 overflow-x-auto">
              <table className="min-w-full border-collapse overflow-hidden rounded-xl border border-slate-200">
                <thead className="bg-blue-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-bold text-slate-700">Student Name</th>
                    <th className="px-4 py-3 text-left text-sm font-bold text-slate-700">Subject</th>
                    <th className="px-4 py-3 text-left text-sm font-bold text-slate-700">Requested Time</th>
                    <th className="px-4 py-3 text-left text-sm font-bold text-slate-700">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingBookings.length === 0 && (
                    <tr>
                      <td colSpan={4} className="px-4 py-8 text-center text-sm text-slate-500">
                        No pending booking requests.
                      </td>
                    </tr>
                  )}
                  {pendingBookings.map((booking) => (
                    <tr key={booking.id} className="border-t border-slate-100">
                      <td className="px-4 py-3 text-sm text-slate-700">{booking.learnerName}</td>
                      <td className="px-4 py-3 text-sm text-slate-700">{booking.subject}</td>
                      <td className="px-4 py-3 text-sm text-slate-700">{booking.requestedTime}</td>
                      <td className="px-4 py-3">
                        <div className="flex gap-2">
                          <Button
                            onClick={() => {
                              setAcceptingBooking(booking);
                              setMeetingLink('');
                            }}
                          >
                            Accept
                          </Button>
                          <Button
                            variant="danger"
                            onClick={() => {
                              setDecliningBooking(booking);
                              setDeclineReason('');
                            }}
                          >
                            Decline
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>

      {isModalOpen && (
        <div className="fixed inset-0 z-30 flex items-center justify-center bg-slate-900/40 px-4">
          <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
            {acceptingBooking && (
              <>
                <h3 className="text-xl font-bold text-slate-900">Accept Booking</h3>
                <p className="mt-1 text-sm text-slate-600">Provide a meeting link for {acceptingBooking.learnerName}.</p>
                <div className="mt-4">
                  <Input
                    label="Meeting Link"
                    value={meetingLink}
                    onChange={(event) => setMeetingLink(event.target.value)}
                    placeholder="https://meet.google.com/..."
                  />
                </div>
                <div className="mt-4 flex justify-end gap-2">
                  <Button variant="outline" onClick={() => setAcceptingBooking(null)}>
                    Cancel
                  </Button>
                  <Button onClick={() => void handleAccept()}>
                    Confirm Accept
                  </Button>
                </div>
              </>
            )}

            {decliningBooking && (
              <>
                <h3 className="text-xl font-bold text-slate-900">Decline Booking</h3>
                <p className="mt-1 text-sm text-slate-600">Share a short reason so the learner can rebook.</p>
                <div className="mt-4">
                  <label className="mb-1.5 block text-xs font-semibold text-gray-800">Decline Reason</label>
                  <textarea
                    value={declineReason}
                    onChange={(event) => setDeclineReason(event.target.value)}
                    rows={4}
                    className="w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                    placeholder="I am fully booked this week."
                  />
                </div>
                <div className="mt-4 flex justify-end gap-2">
                  <Button variant="outline" onClick={() => setDecliningBooking(null)}>
                    Cancel
                  </Button>
                  <Button variant="danger" onClick={() => void handleDecline()}>
                    Confirm Decline
                  </Button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default TutorDashboardPage;
