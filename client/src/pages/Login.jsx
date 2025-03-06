import React, { useState } from 'react';
import './login.css';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../api';

const LoginPage = () => {

  const [loginCredentials, setLoginCredentials] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const handleLogin = async(e) => {
    e.preventDefault();
    try {
      await login(loginCredentials);
      navigate("/dashboard");
    } catch (error) {
      console.log(error);
      setError("Bad Credentials");
    }
  }

  const onInputChange = (e) => {
    setLoginCredentials({...loginCredentials, [e.target.name]:e.target.value});
  }


  return (
    <div className='addUser'>
      <h3>Log in</h3>
      {error && <div className="alert alert-danger" role="alert">{error}</div>}
      <form className='addUserForm' onSubmit={handleLogin}>
         <div className='inputGroup'>
          <label htmlFor='username'>Username</label>
          <input
          type='text'
          name='username'
          value={loginCredentials.username}
          placeholder='Enter your username'
          onChange={onInputChange}
          />
            <label htmlFor='password'>Password</label>
          <input
          type='password'
          name='password'
          autoComplete='off'
          placeholder='Enter your password'
          value={loginCredentials.password}
          onChange={onInputChange}
          />
          <button type="submit" className="btn btn-primary">Log in</button>
        </div>
      </form>
      <div className='login'>
        <p>Don't have an account?</p>
        <Link to="/signup" type="submit" className="btn btn-warning">Sign up</Link>
      </div>
    </div>
  )
}

export default LoginPage
