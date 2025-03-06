import React, { useEffect, useState } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { getUserDetails } from '../api';

const ProtectedRoute = () => {
  const [isValid, setIsValid] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const verifyToken = async () => {
      if(!token) {
        setIsValid(false);
      } else {
        try {
          await getUserDetails();
          setIsValid(true);
        } catch (error) {
          console.error("Token invalid", error);
          localStorage.removeItem("token");
          setIsValid(false);
        }
      }
    }
    verifyToken();
  }, [token]);
  
  if(isValid === null) return null;
  return isValid ? <Outlet/> : <Navigate to="/login" replace/>;
}

export default ProtectedRoute;