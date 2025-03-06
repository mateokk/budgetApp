import React, { useState } from 'react';
import './signup.css';
import { Link, useNavigate } from 'react-router-dom';
import { signup } from '../api';

const SignupPage = () => {
  const [registerCredentials, setRegisterCredentials] = useState({
    username: "",
    email: "",
    password: "",
  });
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const handleRegister = async(e) => {
    e.preventDefault();
    try {
      await signup(registerCredentials);
      navigate("/login");
    } catch (error) {
      setError("Bad Credentials");
    }
  }

  const onInputChange = (e) => {
    setRegisterCredentials(...registerCredentials, {[e.target.name]: e.target.value})
  }


  return (
    <div className='addUser'>
      <h3>Sign up</h3>
      {error && <div className="alert alert-danger" role="alert">{error}</div>}
      <form className='addUserForm' onSubmit={handleRegister}>
        <div className='inputGroup'>
          <label htmlFor='username'>Username</label>
          <input
          type='text'
          name='username'
          value={registerCredentials.username}
          autoComplete='off'
          placeholder='Enter your username'
          onChange={onInputChange}
          />
            <label htmlFor='email'>Email</label>
          <input
          type='text'
          name='email'
          value={registerCredentials.email}
          autoComplete='off'
          placeholder='Enter your email'
          onChange={onInputChange}
          />
            <label htmlFor='password'>Password</label>
          <input
          type='password'
          name='password'
          value={registerCredentials.password}
          autoComplete='off'
          placeholder='Enter your password'
          onChange={onInputChange}
          />
          <button type="submit" className="btn btn-secondary">Sign up</button>
        </div>
      </form>
      <div className='login'>
        <p>Already have an account?</p>
        <Link to="/login" type="submit" className="btn btn-warning">Log in</Link>
      </div>
    </div>
  )
}

export default SignupPage