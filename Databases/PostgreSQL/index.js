const express = require('express')
const bodyParser = require('body-parser')
const app = express()
const port = 3001
const db = require('./queries')

app.use(bodyParser.json())
app.use(
  bodyParser.urlencoded({
    extended: true,
  })
)

app.get('/', (request, response) => {
    response.json({ info: 'Node.js, Express, and Postgres API' })
  })

app.get('/getData', db.getData)
app.get('/getData/:rid', db.getDataByRID)
app.post('/createData', db.createData)
app.put('/updateData/:rid', db.updateData)
app.delete('/deleteData/:rid', db.deleteData)

app.listen(port, () => {
    console.log(`App running on port ${port}.`)
})