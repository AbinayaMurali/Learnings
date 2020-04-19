const Pool = require('pg').Pool
const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: '',//set database name
  password: '',//set password
  port: 5432,
})
const getData = (request, response) => {
    pool.query('SELECT * FROM tbldataflat', (error, results) => {
      if (error) {
        throw error
      }
      response.status(200).json(results.rows)
    })
}
  
  const getDataByRID = (request, response) => {
    const rid = parseInt(request.params.rid)
  
    pool.query('SELECT * FROM tbldata WHERE rid = $1', [rid], (error, results) => {
      if (error) {
        throw error
      }
      response.status(200).json(results.rows)
    })
  }
  
  const createData = (request, response) => {
    const { rid, data } = request.body
    pool.query('INSERT INTO tbldata (rid, data) VALUES ($1, $2)', [rid, data], (error, results) => {
      if (error) {
        throw error
      }
      response.status(201).send(`Data added with ID: ${rid}`)
    })
  }
  
  const updateData = (request, response) => {
    const rid = parseInt(request.params.rid)
    const { data } = request.body
  
    pool.query(
      'UPDATE tbldata SET data = $1 WHERE rid = $2',
      [data, rid],
      (error, results) => {
        if (error) {
          throw error
        }
        response.status(200).send(`Data is modified for : ${rid}`)
      }
    )
  }
  
  const deleteData = (request, response) => {
    const rid = parseInt(request.params.rid)
  
    pool.query('DELETE FROM tbldata WHERE rid = $1', [rid], (error, results) => {
      if (error) {
        throw error
      }
      response.status(200).send(`Data is deleted for : ${rid}`)
    })
  }
  
  module.exports = {
    getData,
    getDataByRID,
    createData,
    updateData,
    deleteData,
}  