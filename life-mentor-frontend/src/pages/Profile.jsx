import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import LoadingSpinner from '../components/common/LoadingSpinner';
import './Profile.css';

const Profile = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        bio: ''
    });
    const [stats, setStats] = useState({
        joinedDate: '',
        streak: 0,
        completedGoals: 0,
        totalCheckins: 0
    });

    useEffect(() => {
        if (!user) {
            navigate('/login');
            return;
        }

        // Load user data
        setFormData({
            name: user.name || '',
            email: user.email || '',
            phone: user.phone || '',
            bio: user.bio || ''
        });

        // Load stats (simulated)
        setStats({
            joinedDate: '2024-01-01',
            streak: 14,
            completedGoals: 8,
            totalCheckins: 45
        });
    }, [user, navigate]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSaveProfile = async () => {
        setLoading(true);
        try {
            // Simulate API call
            await new Promise(resolve => setTimeout(resolve, 1000));
            toast.success('Profile updated successfully!');
            setIsEditing(false);
        } catch (error) {
            toast.error('Failed to update profile');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteAccount = () => {
        if (window.confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
            // Call delete account API here
            toast.info('Account deletion request sent');
        }
    };

    if (!user) {
        return null;
    }

    const getInitials = (name) => {
        return name
            .split(' ')
            .map(word => word[0])
            .join('')
            .toUpperCase()
            .slice(0, 2);
    };

    return (
        <div className="profile-container">
            {/* Header Section */}
            <div className="profile-header">
                <div className="profile-header-content">
                    <div className="profile-avatar-wrapper">
                        <div className="profile-avatar">
                            <span>{getInitials(user.name)}</span>
                        </div>
                        <button
                            className="profile-edit-btn"
                            onClick={() => setIsEditing(!isEditing)}
                        >
                            <svg className="edit-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                        </button>
                    </div>
                    <h1 className="profile-name">{user.name}</h1>
                    <p className="profile-email">{user.email}</p>
                </div>
            </div>

            {/* Content Section */}
            <div className="profile-content-wrapper">
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                    className="profile-grid"
                >
                    {/* Personal Information */}
                    <div className="profile-card">
                        <h2 className="profile-card-title">Personal Information</h2>

                        <div className="form-group">
                            <label className="form-label">Full Name</label>
                            {isEditing ? (
                                <input
                                    type="text"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    className="profile-input"
                                />
                            ) : (
                                <div className="profile-readonly">{formData.name}</div>
                            )}
                        </div>

                        <div className="form-group">
                            <label className="form-label">Email Address</label>
                            <div className="profile-readonly">{formData.email}</div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Phone Number</label>
                            {isEditing ? (
                                <input
                                    type="tel"
                                    name="phone"
                                    value={formData.phone}
                                    onChange={handleInputChange}
                                    className="profile-input"
                                    placeholder="Enter your phone number"
                                />
                            ) : (
                                <div className="profile-readonly">{formData.phone || 'Not provided'}</div>
                            )}
                        </div>

                        <div className="form-group">
                            <label className="form-label">Bio</label>
                            {isEditing ? (
                                <textarea
                                    name="bio"
                                    value={formData.bio}
                                    onChange={handleInputChange}
                                    className="profile-textarea"
                                    placeholder="Tell us about yourself..."
                                />
                            ) : (
                                <div className="profile-readonly profile-bio">
                                    {formData.bio || 'No bio provided'}
                                </div>
                            )}
                        </div>

                        {isEditing && (
                            <button
                                onClick={handleSaveProfile}
                                disabled={loading}
                                className="profile-save-btn"
                            >
                                {loading ? (
                                    <>
                                        <LoadingSpinner size="small" className="btn-spinner" />
                                        Saving...
                                    </>
                                ) : (
                                    'Save Changes'
                                )}
                            </button>
                        )}
                    </div>

                    {/* Stats Card */}
                    <div className="profile-card">
                        <h2 className="profile-card-title">Your Stats</h2>

                        <div className="profile-stats">
                            <div className="stats-item">
                                <span className="stats-label">Joined On</span>
                                <span className="stats-value">{stats.joinedDate}</span>
                            </div>

                            <div className="stats-item">
                                <span className="stats-label">Current Streak</span>
                                <span className="stats-value">{stats.streak} days</span>
                            </div>

                            <div className="stats-item">
                                <span className="stats-label">Goals Completed</span>
                                <span className="stats-value">{stats.completedGoals}</span>
                            </div>

                            <div className="stats-item">
                                <span className="stats-label">Total Check-ins</span>
                                <span className="stats-value">{stats.totalCheckins}</span>
                            </div>
                        </div>

                        <button
                            onClick={() => navigate('/dashboard')}
                            className="profile-stats-btn"
                        >
                            View Detailed Stats
                        </button>
                    </div>

                    {/* Account Settings */}
                    <div className="profile-card">
                        <h2 className="profile-card-title">Account Settings</h2>

                        <div className="profile-actions">
                            <button
                                onClick={() => navigate('/change-password')}
                                className="profile-action-btn"
                            >
                                Change Password
                            </button>

                            <button
                                onClick={() => toast.info('Coming soon!')}
                                className="profile-action-btn"
                            >
                                Notification Settings
                            </button>

                            <button
                                onClick={() => toast.info('Coming soon!')}
                                className="profile-action-btn"
                            >
                                Privacy Settings
                            </button>

                            <button
                                onClick={() => logout()}
                                className="profile-logout-btn"
                            >
                                Logout
                            </button>
                        </div>

                        {/* Danger Zone */}
                        <div className="danger-zone">
                            <h3 className="danger-title">Danger Zone</h3>
                            <p className="danger-description">
                                Once you delete your account, there is no going back. Please be certain.
                            </p>
                            <button
                                onClick={handleDeleteAccount}
                                className="danger-btn"
                            >
                                Delete Account
                            </button>
                        </div>
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default Profile;