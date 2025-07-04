function Welcome({ name = 'Guest', myFavoriteColor = 'green' }) {
   return <h1 style={{ color: myFavoriteColor }}>Hi, {name}!</h1>;
}

export default Welcome;