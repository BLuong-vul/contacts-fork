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
		      <li onClick={() => setActiveSection('General')} style={{ cursor: 'pointer', marginBottom: '10px' }}> General </li>
		      {/*<li onClick={() => setActiveSection('Privacy')} style={{ cursor: 'pointer', marginBottom: '10px' }}> Privacy </li>*/}
		      {/*<li onClick={() => setActiveSection('Settings')} style={{ cursor: 'pointer', marginBottom: '10px' }}> Settings </li>*/}
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
  const [userInfo, setUserInfo] = useState({
    displayName: '',
    bio: '',
    location: '',
    qualifications: '',
    occupation: '',
  })

  const [isEditing, setIsEditing] = useState({
    displayName: false,
    bio: false,
    location: false,
    qualifications: false,
    occupation: false
  });

  const [show, setShow] = useState({
    location: false,
    qualifications: false,
    occupation: false,
    birthdate: false
  });

  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [selectedImage, setSelectedImage] = useState(null);

  const [showBannerMenu, setShowBannerMenu] = useState(false);
  const [selectedBanner, setSelectedBanner] = useState(null);

  // Fetch user data on mount
  useEffect(() => {
      const fetchUserId = async () => {
          try {
              const currentUserInfo = await Fetch.getCurrentUserInfo();
              setUserInfo(currentUserInfo);
              console.log(currentUserInfo);
          } catch (error) {
              console.error('Error fetching ID:', error);
              throw error;
          }
      };
      fetchUserId();
  }, []);

  // On submit any text field
  const handleFieldSubmit = async (field) => {
    if (isEditing[field]){
      await Fetch[`update${field.charAt(0).toUpperCase() + field.slice(1)}`](userInfo[field]);
    }
    setIsEditing(prevState => ({
          ...prevState,
          [field]: !prevState[field]
    }));
  }

  // On change to any text field
  const handleChange = (e, field) => {
    setUserInfo(prevState => ({
      ...prevState,
      [field]: e.target.value
    }));
  };

  /* ===== PROFILE PICTURE UPDATES ===== */
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedImage(file); // Generates preview URL
    }
  };

  const handleImageSave = async () => {
    console.log(selectedImage);
    const imageFileName = await Fetch.uploadMedia(selectedImage);
    console.log(await Fetch.updateProfilePictureFileName(imageFileName));
  }

  // TODO: Change this to actually removing the user's profile picture instead of just clearing it
  const handleImageRemove = () => {
    setSelectedImage(null);
  }

  const handleMenuToggle = () => {
    setShowProfileMenu(!showProfileMenu);
    setShowBannerMenu(false);
  };

  /* ===== PROFILE BANNER UPDATES ===== */
  const handleBannerChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedBanner(file); // Generates preview URL
    }
  };

  const handleBannerSave = async () => {
    console.log(selectedBanner);
    const bannerFileName = await Fetch.uploadMedia(selectedBanner);
    console.log(await Fetch.updateProfileBannerFileName(bannerFileName));
  }

  // TODO: Change this to actually removing the user's banner picture instead of just clearing it
  const handleBannerRemove = () => {
    setSelectedBanner(null);
  }

  const handleBannerMenuToggle = () => {
    setShowBannerMenu(!showBannerMenu);
    setShowProfileMenu(false);
  };


  // Toggle for checkboxes
  const toggleShow = (field) => {
    setShow(prevState => ({
      ...prevState,
      [field]: !prevState[field]
    }));
  };

  const handleLogout = () => {
    Fetch.logout();
    window.location.href = '/social-media-app';
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      {/* Display Name */}
      <label style={{ flex: 1 }}>Display Name:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={userInfo.displayName}
          onChange={(e) => handleChange(e, 'displayName')}
          readOnly={!isEditing.displayName}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditing.displayName ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.displayName ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('displayName')} style={{ marginLeft: '10px' }}>
          {isEditing.displayName ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Bio */}
      <label style={{ flex: 1 }}>Bio:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <textarea
          value={userInfo.bio}
          onChange={(e) => handleChange(e, 'bio')}
          readOnly={!isEditing.bio}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            minHeight: '80px',
            backgroundColor: isEditing.bio ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.bio ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('bio')} style={{ marginLeft: '10px' }}>
          {isEditing.bio ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Location */}
      <label style={{ flex: 1 }}>Location:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={userInfo.location}
          onChange={(e) => handleChange(e, 'location')}
          readOnly={!isEditing.location}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditing.location ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.location ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('location')} style={{ marginLeft: '10px' }}>
          {isEditing.location ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Occupation */}
      <label style={{ flex: 1 }}>Occupation:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={userInfo.occupation}
          onChange={(e) => handleChange(e, 'occupation')}
          readOnly={!isEditing.occupation}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditing.occupation ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.occupation ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('occupation')} style={{ marginLeft: '10px' }}>
          {isEditing.occupation ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Birthday display checkbox */}
      {/*
      <div style={{ marginTop: '20px', marginBottom: '20px' }}>
        <label style={{ display: 'block' }}>
          <input
            type="checkbox"
            checked={show.birthdate}
            onChange={() => toggleShow('birthdate')}
          />
          Display birthdate on profile
        </label>
      </div>
      */}

      {/* Profile picture / banner */}
      <div className="relative flex gap-4 mb-5 mt-8">
        {/* CHANGE PROFILE PICTURE */}
        <div className="flex">
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none"
            onClick={handleMenuToggle}
          >
            Change Profile Picture
          </button>
          {showProfileMenu && (
            <div className="absolute top-full mt-2 bg-slate-500 p-4 rounded-lg shadow-lg z-10">
              <h4 className="text-lg text-center text-slate-300 mb-3">Select New Profile Picture</h4>
              <div className="flex flex-col items-center">
                {selectedImage ? (
                  <div className="mb-3 mr-4">
                    <img
                      src={URL.createObjectURL(selectedImage)}
                      alt="Preview"
                      className="w-24 h-24 rounded-full object-cover"
                    />
                  </div>
                ) : (
                  <p className="text-slate-300 mb-3">No image selected</p>
                )}
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="block w-32 mt-2 text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                />
              </div>
              <div className="flex justify-center">
                <button
                  className="mt-3 mr-2 px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 focus:outline-none"
                  onClick={() => {
                    handleImageSave();
                    setShowProfileMenu(false);
                  }}
                >
                  Save
                </button>
                <button
                  className="mt-3 ml-2 mr-2 px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none"
                  onClick={() => {
                    handleImageRemove();
                  }}
                >
                  Clear
                </button>
                </div>
            </div>
          )}
        </div>
        {/* CHANGE PROFILE BANNER */}
        <div className="flex">
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none"
            onClick={handleBannerMenuToggle}
          >
            Change Profile Banner
          </button>
          {showBannerMenu && (
            <div className="absolute top-full mt-2 bg-slate-500 p-4 rounded-lg shadow-lg z-10">
              <h4 className="text-lg text-center text-slate-300 mb-3">Select New Banner Picture</h4>
              <div className="flex flex-col items-center">
                {selectedBanner ? (
                  <div className="mb-3 mr-4">
                    <img
                      src={URL.createObjectURL(selectedBanner)}
                      alt="Preview"
                      className="w-96 h-48 rounded-sm object-cover"
                    />
                  </div>
                ) : (
                  <p className="text-slate-300 mb-3">No image selected</p>
                )}
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleBannerChange}
                  className="block w-32 mt-2 text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                />
              </div>
              <div className="flex justify-center">
                <button
                  className="mt-3 mr-2 px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 focus:outline-none"
                  onClick={() => {
                    handleBannerSave();
                    setShowBannerMenu(false);
                  }}
                >
                  Save
                </button>
                <button
                  className="mt-3 ml-2 mr-2 px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none"
                  onClick={() => {
                    handleBannerRemove();
                  }}
                >
                  Clear
                </button>
                </div>
            </div>
          )}
        </div>
      </div>


      {/* Logout button */}
      <div className="mt-5 w-24 px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 focus:outline-none">
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
            <label style={{ flex: 1 }}>Messages</label>
            <label style={{ display: 'block', marginTop: '10px' }}>
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