import React, { useEffect, useState } from 'react'
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import Form from 'react-bootstrap/Form';
import { addTransaction, getCategories } from '../api';

const AddTransaction = ({show, onHide, onAddTransaction}) => {
 
  const [transaction, setTransaction] = useState({
    description: "",
    amount: "",
    categoryId: "",
    date: ""
  });
  const [categories, setCategories] = useState([]);

  const onInputChange = (e) => {
    setTransaction({...transaction, [e.target.name]:e.target.value});
  }

  const submitTransaction = async(e) => {
    e.preventDefault();
    try {
      const response = await addTransaction(transaction);
      onAddTransaction(response.data);
    } catch (error) {
      console.log("Adding transaction error: ", error);
    }
    onHide();
  }

  useEffect( () => {
    const fetchData = async() => {
      try {
        const result = await getCategories();
        setCategories(result.data);
      } catch (error) {
        console.log("Category fetching error: ", error);
      }      
    };
    fetchData();
  }, []);

  useEffect( () => {
    if(show) {
      setTransaction({
        description: "",
        amount: "",
        categoryId: "",
        date: ""
      });
    }
  }, [show]);
  

  return (
    <Modal  
    show={show}
    onHide={onHide}
    aria-labelledby="addTransaction"
    centered
  >
    <Modal.Header closeButton>
      <Modal.Title id="addTransaction">
        Add new transaction
      </Modal.Title>
    </Modal.Header>
    <Modal.Body>
    <Form onSubmit={submitTransaction}>
      <Form.Group className="mb-3" controlId="description">
        <Form.Label>Transaction description</Form.Label>
        <Form.Control 
        type="text" 
        name='description'
        placeholder="Enter description" 
        value={transaction.description}
        onChange={onInputChange}/>
      </Form.Group>
      <Form.Group className="mb-3" controlId="date">
      <Form.Label>Category</Form.Label>
      <Form.Select 
      aria-label="category"
      name='categoryId'
      onChange={onInputChange}>
        <option value="">Set category</option>
        {
          categories.map((category) => (
            <option key={category.id} value={category.id}>{category.name}</option>
          ))
        }
    </Form.Select>
      </Form.Group>
      <Form.Group className="mb-3" controlId="amount">
        <Form.Label>Amount</Form.Label>
        <Form.Control
         type="number" 
         placeholder="Enter amount in PLN" 
         name='amount'
         value={transaction.amount}
         onChange={onInputChange}/>
      </Form.Group>
      <Form.Group className="mb-3" controlId="date">
        <Form.Label>Date</Form.Label>
        <Form.Control
         type="date" 
         name='date'
         value={transaction.date} 
         onChange={onInputChange}/>
      </Form.Group>
      <Form.Group className='d-flex justify-content-evenly'>
        <Button variant="outline-success" type="submit">
          Confirm
        </Button>
        <Button variant="outline-danger" onClick={onHide}>
          Cancel
        </Button>
      </Form.Group>
    </Form>
    </Modal.Body>
  </Modal>
  )
}

export default AddTransaction;