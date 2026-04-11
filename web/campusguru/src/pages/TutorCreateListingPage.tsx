import { useState } from 'react';
import type { ChangeEvent, FormEvent } from 'react';
import MarketplaceNavbar from '../components/MarketplaceNavbar';
import Button from '../components/Button';
import Input from '../components/Input';
import { marketplaceService } from '../services/marketplaceService';

const TutorCreateListingPage = () => {
  const [subject, setSubject] = useState('');
  const [availableTime, setAvailableTime] = useState('');
  const [contactInfo, setContactInfo] = useState('');
  const [proofFileName, setProofFileName] = useState('');
  const [proofUrl, setProofUrl] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const mockUploadProof = async (file: File) => {
    const extension = file.name.split('.').pop() || 'pdf';
    return `https://dummy-proof.campusguru.local/uploads/${Date.now()}.${extension}`;
  };

  const handleFileChange = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    setProofFileName(file.name);
    setError('');

    try {
      const url = await mockUploadProof(file);
      setProofUrl(url);
    } catch {
      setError('Failed to prepare proof file. Please try again.');
    }
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage('');
    setError('');

    if (!subject.trim() || !availableTime.trim() || !contactInfo.trim() || !proofUrl.trim()) {
      setError('Please complete all fields and upload proof of competence.');
      return;
    }

    setIsSubmitting(true);
    try {
      await marketplaceService.createListing({
        subject,
        availableTime,
        contactInfo,
        proofOfCompetenceUrl: proofUrl,
      });

      setSubject('');
      setAvailableTime('');
      setContactInfo('');
      setProofFileName('');
      setProofUrl('');
      setMessage('Listing submitted for review successfully.');
    } catch (requestError: any) {
      setError(requestError?.response?.data?.message || 'Failed to create listing.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <MarketplaceNavbar title="Create Tutor Listing" />

      <main className="mx-auto w-full max-w-3xl px-4 py-8 sm:px-6 lg:px-8">
        <section className="rounded-2xl border border-blue-100 bg-white p-6 shadow-sm">
          <h2 className="text-2xl font-bold text-slate-900">Offer Your Tutoring Session</h2>
          <p className="mt-1 text-sm text-slate-600">Publish your availability and expertise for learners to book.</p>

          {message && <p className="mt-4 rounded-md bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p>}
          {error && <p className="mt-4 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}

          <form className="mt-6 space-y-1" onSubmit={handleSubmit}>
            <Input
              label="Subject Name"
              name="subject"
              value={subject}
              onChange={(event) => setSubject(event.target.value)}
              placeholder="Computer Architecture"
            />

            <Input
              label="Available Time"
              name="availableTime"
              value={availableTime}
              onChange={(event) => setAvailableTime(event.target.value)}
              placeholder="Mon, Wed, Fri 3:00 PM - 5:00 PM"
            />

            <Input
              label="Contact Information"
              name="contactInfo"
              value={contactInfo}
              onChange={(event) => setContactInfo(event.target.value)}
              placeholder="MS Teams: tutor.name / Facebook: tutor.handle"
            />

            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-gray-800">Proof of Competence</label>
              <input
                type="file"
                accept=".pdf,.png,.jpg,.jpeg"
                className="h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                onChange={handleFileChange}
              />
              <p className="mt-1 text-xs text-slate-500">
                {proofFileName ? `Selected: ${proofFileName}` : 'Upload certificate, transcript, or relevant document.'}
              </p>
            </div>

            <Button type="submit" isLoading={isSubmitting} className="mt-2 w-full">
              Submit for Review
            </Button>
          </form>
        </section>
      </main>
    </div>
  );
};

export default TutorCreateListingPage;
