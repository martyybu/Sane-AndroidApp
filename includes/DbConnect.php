<?php
//Creates a connect class
class DbConnect {
  //Creates a connection variable
  private $con;

  //Creates a constructor
  function __construct()
  {

            
  }

  //Connects to the DB using constant values (DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
  function connect()
  {

            include_once dirname(__FILE__).'/Constants.php';
            $this->con = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
            
            if(mysqli_connect_errno()) {
                echo "Failed to connect to the database: ".mysqli_connect_err();
            }
            
            return $this->con;
  }

}

