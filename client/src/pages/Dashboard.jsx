import React, { useEffect, useState } from 'react'
import { deleteTransaction, getCategories, getTransactions } from '../api';
import AddTransaction from './AddTransaction';
import EditTransaction from './EditTransaction';

const Dashboard = () => {
  
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [modalShow, setModalShow] = useState(false);
  const [editModalShow, setEditModalShow] = useState(false);
  const [editedTransaction, setEditedTransaction] = useState("");

  useEffect(() => {
    const fetchData = async() => {
      try {
        const response = await getTransactions(selectedCategory);
        setTransactions(response.data);
        const categoryResponse = await getCategories();
        setCategories(categoryResponse.data);
      } catch (error) {
        console.log("Fetch transactions error ", error);
      }
    }
    fetchData();
  }, [selectedCategory]);

  const handleAddTransaction = (newTransaction) => {
    setTransactions([...transactions, newTransaction]);
  }

  const handleDeleteTransaction = async(id) => {
    try {
      await deleteTransaction(id);
      setTransactions(transactions.filter(transaction => transaction.id !== id));
    } catch (error) {
      console.log("Deleting transaction error ", error);
    }
  };

  const handleUpdateTransaction = (updatedTransaction) => {
    setTransactions(transactions.map((transaction) => (
      transaction.id === updatedTransaction.id ? updatedTransaction : transaction
    )));
  }

  const handleEditTransaction = (transaction) => {
    setEditedTransaction(transaction);
    setEditModalShow(true);
  }
  
  return (
   <div className='container'>
    <div className='my-5 p-4'>
      <div className="input-group mb-3 d-flex">
        <div className="input-group-prepend">
          <label className="input-group-text" htmlFor="categorySelect">Category</label>
        </div>
        <select className="custom-select" id="categorySelect" onChange={(e) => setSelectedCategory(e.target.value)}>
          <option value="">all</option>
          {
            categories.map((category) => (
              <option key={category.id} value={category.id}>{category.name}</option>
            ))
          }
        </select>
        <div className='ms-auto'>
          <button type="button" className="btn btn-success mx-2" onClick={() => setModalShow(true)}>
              Add new transaction
          </button>
          <AddTransaction
          show={modalShow}
          onHide={() => setModalShow(false)}
          onAddTransaction={handleAddTransaction}
          />
          <EditTransaction
          show={editModalShow}
          onHide={() => setEditModalShow(false)}
          onEditTransaction={handleUpdateTransaction}
          editedTransaction={editedTransaction}
          />
        </div>
      </div>
      <table className='table table-hover border shadow'>
        <thead>
          <tr>
            <th scope='col'>#</th>
            <th scope='col'>Date</th>
            <th scope='col'>Description</th>
            <th scope='col'>Category</th>
            <th scope='col'>Amount [PLN]</th>
            <th scope='col'>Action</th>
          </tr>
        </thead>
        <tbody>
          {
            transactions.map((transaction, rowId) => (
              <tr key={rowId}>
                <th scope='row' key={rowId}>{rowId+1}</th>
                <td>{transaction.date}</td>
                <td>{transaction.description}</td>
                <td>{transaction.categoryName}</td>
                <td>{transaction.amount}</td>
                <td>
                  <button type="button" className="btn btn-warning mx-1" onClick={() => handleEditTransaction(transaction)}>Edit</button>
                  <button type="button" className="btn btn-danger mx-1" onClick={() => handleDeleteTransaction(transaction.id)}>Delete</button>
                </td>
              </tr>
            ))
          }

        </tbody>
      </table>
    </div>
   </div>
  );
}

export default Dashboard