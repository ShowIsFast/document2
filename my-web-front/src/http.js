import axios from 'axios'
import config from './config'

axios.defaults.baseURL = config.api
axios.defaults.timeout = 3000
axios.defaults.withCredentials = true

