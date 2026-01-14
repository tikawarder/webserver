import React from 'react';
import UserItem from './UserItem';

function UserList({ users, loading, error, onRefresh, page, totalPages, onPageChange }) {

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

            {!loading && !error && (
                <div style={{ marginTop: '15px', display: 'flex', justifyContent: 'center', gap: '15px', alignItems: 'center' }}>

                    <button
                        onClick={() => onPageChange(page - 1)}
                        disabled={page === 0}
                        className="pagination-button"
                    >
                        ⬅️ previous
                    </button>

                    <span>
                        <strong>{page + 1}.</strong> page / {totalPages}
                    </span>

                    <button
                        onClick={() => onPageChange(page + 1)}
                        disabled={page >= totalPages - 1}
                        className="pagination-button"
                    >
                        Next ➡️
                    </button>

                </div>
            )}
        </div>
    );
}

export default UserList;