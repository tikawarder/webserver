import React from 'react';
import '../App.css';

function MainLayout({ welcomePanel, formPanel, listPanel, counterPanel }) {
  return (
    <div className="app-container">

      <div className="panel panel-welcome">
        {welcomePanel}
      </div>

      <div className="panel panel-form">
        {formPanel}
      </div>

      <div className="panel panel-list">
        {listPanel}
      </div>

      <div className="panel panel-counter">
        {counterPanel}
      </div>

    </div>
  );
}

export default MainLayout;