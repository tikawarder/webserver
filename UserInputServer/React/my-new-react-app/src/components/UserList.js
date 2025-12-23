import React from 'react';
import UserItem from './UserItem';

function UserList({ users, loading, error, onRefresh }) {

    return (
        <div className="user-list-container">
            <div className="user-list-header">
                <h2>Users</h2>
                <button
                    onClick={onRefresh}
                    disabled={loading}
                    className="refresh-button"
                >
                    {loading ? 'Refresh...' : '↻ Refresh'}
                </button>
            </div>

            {error && <p className="error-message">Error: {error}</p>}

            <div className="scrollable-list">
                {users.map(user => (
                    <UserItem
                        key={user.id}
                        id={user.id}
                        name={user.name}
                        birthDay={user.dob || user.birthDay}
                        city={user.city}
                    />
                ))}
            </div>

            {!loading && users.length === 0 && !error && (
                <p className="empty-message">No data.</p>
            )}
        </div>
    );
}

export default UserList;