'use client';
import React, { useEffect, useState } from 'react';
import * as Fetch from '../../../components/Functions';
import { FaEdit, FaCheck } from 'react-icons/fa'; 


export default function Account() {
	const [activeSection, setActiveSection] = useState('General');



	// Function to render content based on active section
	const renderContent = () => {
	  switch (activeSection) {
	    case 'General':
	      return <GeneralContent />;
	    case 'Privacy':
	      return <PrivacyContent />;
	    case 'Settings':
	      return <SettingsContent />;
	    default:
	      return null;
	  }
	};
	
	return (
		<>
		<div style={{ display: 'flex', minHeight: '100vh' }}>
		  {/* navigation sidebar */}
		  <div style={{ width: '200px', padding: '20px', backgroundColor: '#223344' }}>
		    <ul style={{ listStyle: 'none', padding: 0 }}>
		      <li onClick={() => setActiveSection('General')} style={{ cursor: 'pointer', marginBottom: '10px' }}>
		        General
		      </li>
		      <li onClick={() => setActiveSection('Privacy')} style={{ cursor: 'pointer', marginBottom: '10px' }}>
		        Privacy
		      </li>
		      <li onClick={() => setActiveSection('Settings')} style={{ cursor: 'pointer', marginBottom: '10px' }}>
		        Settings
		      </li>
		    </ul>
		  </div>

		  {/* content area */}
		  <div style={{ flexGrow: 1, padding: '20px' }}>
		    {renderContent()}
		  </div>
		</div>
		</>
	);
}



function GeneralContent() {
  const [displayName, setDisplayName] = useState('');
  const [bio, setBio] = useState('');
  const [location, setLocation] = useState('');
  const [qualifications, setQualifications] = useState('');
  const [occupation, setOccupation] = useState('');

  const [isEditingDisplayName, setIsEditingDisplayName] = useState(false);
  const [isEditingBio, setIsEditingBio] = useState(false);
  const [isEditingLocation, setIsEditingLocation] = useState(false);
  const [isEditingQualifications, setIsEditingQualifications] = useState(false);
  const [isEditingOccupation, setIsEditingOccupation] = useState(false);

  const [showLocation, setShowLocation] = useState(false);
  const [showQualifications, setShowQualifications] = useState(false);
  const [showOccupation, setShowOccupation] = useState(false);
  const [showBirthday, setShowBirthday] = useState(false);

  // const [bio, setBio] = useState('');

  // Initialize everything on mount
  useEffect(() => {
      const fetchUserId = async () => {
          try {
              const currentUserInfo = await Fetch.getCurrentUserInfo();
              setBio(currentUserInfo.bio);
              console.log(currentUserInfo);
          } catch (error) {
              console.error('Error fetching ID:', error);
              throw error;
          }
      };

      fetchUserId();
  }, []);

  const handleBioSubmit = async () => {
    if (isEditingBio){
      await Fetch.updateBio(bio);
    }
    setIsEditingBio(!isEditingBio);
  }

  const handleLogout = () => {
    Fetch.logout();
    window.location.href = '/social-media-app';
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <h3>General Settings</h3>

      {/* Display Name */}
      <label style={{ flex: 1 }}>Display Name:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={displayName}
          onChange={(e) => setDisplayName(e.target.value)}
          readOnly={!isEditingDisplayName}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingDisplayName ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingDisplayName ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingDisplayName(!isEditingDisplayName)} style={{ marginLeft: '10px' }}>
          {isEditingDisplayName ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Bio */}
      <label style={{ flex: 1 }}>Bio:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <textarea
          value={bio}
          onChange={(e) => setBio(e.target.value)}
          readOnly={!isEditingBio}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            minHeight: '80px',
            backgroundColor: isEditingBio ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingDisplayName ? 'text' : 'default',
          }}
        />
        <button onClick={handleBioSubmit} style={{ marginLeft: '10px' }}>
          {isEditingBio ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Location */}
      <label style={{ flex: 1 }}>Location:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={location}
          onChange={(e) => setLocation(e.target.value)}
          readOnly={!isEditingLocation}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingLocation ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingDisplayName ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingLocation(!isEditingLocation)} style={{ marginLeft: '10px' }}>
          {isEditingLocation ? <FaCheck /> : <FaEdit />}
        </button>
      </div>
      <label style={{ display: 'block', marginTop: '0px', marginBottom: '15px' }}>
          <input
            type="checkbox"
            checked={showLocation}
            onChange={() => setShowLocation(!showLocation)}
          />
          Display on profile
      </label>

      {/* Qualifications */}
      <label style={{ flex: 1 }}>Qualifications:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={qualifications}
          onChange={(e) => setQualifications(e.target.value)}
          readOnly={!isEditingQualifications}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingQualifications ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingDisplayName ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingQualifications(!isEditingQualifications)} style={{ marginLeft: '10px' }}>
          {isEditingQualifications ? <FaCheck /> : <FaEdit />}
        </button>
      </div>
      <label style={{ display: 'block', marginBottom: '15px' }}>
          <input
            type="checkbox"
            checked={showQualifications}
            onChange={() => setShowQualifications(!showQualifications)}
          />
          Display on profile
      </label>

      {/* Occupation */}
      <label style={{ flex: 1 }}>Occupation:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={occupation}
          onChange={(e) => setOccupation(e.target.value)}
          readOnly={!isEditingOccupation}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingOccupation ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingDisplayName ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingOccupation(!isEditingOccupation)} style={{ marginLeft: '10px' }}>
          {isEditingOccupation ? <FaCheck /> : <FaEdit />}
        </button>
      </div>
      <label style={{ display: 'block', marginBottom: '15px' }}>
        <input
          type="checkbox"
          checked={showOccupation}
          onChange={() => setShowOccupation(!showOccupation)}
        />
        Display on profile
      </label>

      {/* Birthday display checkbox */}
      <div style={{ marginTop: '20px', marginBottom: '20px' }}>
        <label style={{ display: 'block' }}>
          <input
            type="checkbox"
            checked={showBirthday}
            onChange={() => setShowBirthday(!showBirthday)}
          />
          Display birthday on profile
        </label>
      </div>

      {/* Profile picture / banner */}
      <div style={{ display: 'flex', gap: '15px', marginBottom: '20px' }}>
        <button style={{ padding: '10px', cursor: 'pointer' }}>Change Profile Picture</button>
        <button style={{ padding: '10px', cursor: 'pointer' }}>Change Profile Banner</button>
      </div>

      {/* Logout button */}
      <div className="mt-5">
        <button onClick={handleLogout}>Log Out</button>
      </div>
    </div>
  );
}



function PrivacyContent() {
      // state for buttons/checkbox
      const [messagesOption, setMessagesOption] = useState('');
      const [requestsOption, setRequestsOption] = useState(false);
      
      const handleChange = (event) => {
        setMessagesOption(event.target.value);
      };


      return (
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
          <div style={{ marginTop: '15px', marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option1"
                checked={messagesOption === 'option1'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Allow messages from anyone
            </label>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option2"
                checked={messagesOption === 'option2'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Allow messages from mutuals only
            </label>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option3"
                checked={messagesOption === 'option3'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Allow messages from followers only
            </label>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option4"
                checked={messagesOption === 'option4'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Disable messages
            </label>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block' }}>
              <input
                type="checkbox"
                checked={requestsOption}
                onChange={() => setRequestsOption(!requestsOption)}
                style={{marginRight: '8px'}}
              />
              Blocked messages become requests
            </label>
          </div>
        </div>
      );
}

function SettingsContent() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [isEditingUsername, setIsEditingUsername] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);

  const handleDelete = () => {
    
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>

      {/* Username */}
      <label style={{ flex: 1 }}>Username:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          readOnly={!isEditingUsername}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingUsername ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingUsername ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingUsername(!isEditingUsername)} style={{ marginLeft: '10px' }}>
          {isEditingUsername ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

    {/* Password */}
      <label style={{ flex: 1 }}>Password:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          readOnly={!isEditingPassword}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingPassword ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingPassword ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingUPassword(!isEditingPassword)} style={{ marginLeft: '10px' }}>
          {isEditingPassword ? <FaCheck /> : <FaEdit />}
        </button>
      </div>



      {/* Delete Account */}
      <div className="mt-5">
        <button onClick={handleDelete}>Delete Account</button>
      </div>
    </div>
  );
}