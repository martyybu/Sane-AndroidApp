<?php
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\ResponseInterface as Response;

require '../vendor/autoload.php';

require '../includes/DbOperations.php';
$app = new \Slim\App([
    'settings' => [
        'displayErrorDetails' => true
    ]
]);

// Creates a user in the DB
$app->post('/createuser', function (Request $request, Response $response) {
    //Checks if the data submitted have all required parameters: username, password, email.
    if (!haveEmptyParameters(array('username', 'password', 'email'), $request, $response)) {

        //Gets the data
        $request_data = $request->getParsedBody();

        //Arranges the data
        $username = $request_data['username'];
        $password = $request_data['password'];
        $email = $request_data['email'];

        //Encrypts the password
        $hash_password = password_hash($password, PASSWORD_DEFAULT);

        //Gets the DbOperations object
        $db = new DbOperations;

        //Calls the function to create a user.
        $result = $db->createUser($username, $hash_password, $email);

        //On response acts accordingly. USER_CREATED, USER_FAILURE and others are constants.
        if ($result == USER_CREATED) {

            //If user is created gets the ID of the created user
            $ID = $db->getIDByUsername($username);
            //Sets the image and about
            $Image_ID = null;
            $about = "";

            //Creates a profile for the new user.
            $db->createProfile($ID, $Image_ID, $about);

            //Sends a message back. It can be viewed both in the app and in postman
            $message = array();
            $message['error'] = false;
            $message['message'] = 'User created successfully! Welcome.';

            $response->write(json_encode($message));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(201);
        } else if ($result == USER_FAILURE) {

            $message = array();
            $message['error'] = true;
            $message['message'] = 'Error occurred';

            $response->write(json_encode($message));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(423);
        } else if ($result == USER_EXISTS) {

            $message = array();
            $message['error'] = true;
            $message['message'] = 'User already exists with this username or password.';

            $response->write(json_encode($message));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(422);
        }
    }
});

//Creates a therapist
$app->post('/createtherapist', function (Request $request, Response $response) {
    if (!haveEmptyParameters(array('username', 'password', 'email', 'name', 'surname', 'DoB', 'phoneNumber', 'discipline', 'valid'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $username = $request_data['username'];
        $password = $request_data['password'];
        $email = $request_data['email'];
        $name = $request_data['name'];
        $surname = $request_data['surname'];
        $DoB = $request_data['DoB'];
        $phoneNumber = $request_data['phoneNumber'];
        $discipline = $request_data['discipline'];
        $valid = $request_data['valid'];

        $hash_password = password_hash($password, PASSWORD_DEFAULT);

        $db = new DbOperations;

        $result = $db->createUser($username, $hash_password, $email);

        if ($result == USER_FAILURE) {

            $message = array();
            $message['error'] = true;
            $message['message'] = 'Error occurred';

            $response->write(json_encode($message));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(423);
        } else if ($result == USER_EXISTS) {

            $message = array();
            $message['error'] = true;
            $message['message'] = 'User already exists with this username or password.';

            $response->write(json_encode($message));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(422);
        } else if ($result == USER_CREATED) {

            $ID = $db->getIDByUsername($username);
            $Image_ID = null;
            $about = "";
            $db->createProfile($ID, $Image_ID, $about);

            $result = $db->addUserAsATherapist($ID, $name, $surname, $DoB, $phoneNumber, $discipline, $valid);

            if ($result == USER_CREATED) {

                $message = array();
                $message['error'] = false;
                $message['message'] = 'Therapist user created successfully! Welcome.';

                $response->write(json_encode($message));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(201);
            } else if ($result == USER_FAILURE) {

                $message = array();
                $message['error'] = true;
                $message['message'] = 'Error occurred';

                $response->write(json_encode($message));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(423);
            } else if ($result == USER_EXISTS) {

                $message = array();
                $message['error'] = true;
                $message['message'] = 'Therapist already exists with this username or email.';

                $response->write(json_encode($message));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(422);
            }
        }
    }
});

//Gets user username
$app->post('/getuserusername', function (Request $request, Response $response) {

    if (!haveEmptyParameters(array('ID'), $request, $response)) {
        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];

        $db = new DbOperations;

        $username = $db->getUserUsernameByID($ID);

        if ($username != null) {

            $response_data['error'] = false;
            $response_data['username'] = $username;

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        }
    }
});

//Gets messages
$app->post('/getmessages', function (Request $request, Response $response) {

    if (!haveEmptyParameters(array('Chat_ID'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $Chat_ID = $request_data['Chat_ID'];

        $db = new DbOperations;

        $messages = $db->getMessagesByChatID($Chat_ID);

        $response_data = array();
        $response_data['error'] = false;
        $response_data['messages'] = $messages;

        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
    }
});

//Sends messages
$app->post('/sendmessage', function (Request $request, Response $response) {

    if (!haveEmptyParameters(array('ID', 'Chat_ID', 'message'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];
        $Chat_ID = $request_data['Chat_ID'];
        $message = $request_data['message'];

        $db = new DbOperations;

        $result = $db->sendMessage($ID, $Chat_ID, $message);

        if ($result == true) {
            $response_data = array();
            $response_data['error'] = false;
            $response_data['message'] = 'Message sent successfully.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else {
            $response_data = array();

            $response_data['error'] = true;
            $response_data['message'] = 'Message was not sent';

            $response->write(json_encode($response_data));
        }
    }
});

//User login
$app->post('/userlogin', function (Request $request, Response $response) {

    if (!haveEmptyParameters(array('username', 'password'), $request, $response)) {
        $request_data = $request->getParsedBody();

        $username = $request_data['username'];
        $password = $request_data['password'];

        $db = new DbOperations;

        $result = $db->userLogin($username, $password);

        if ($result == USER_AUTHENTICATED) {

            $user = $db->getUserByUsername($username);
            $response_data = array();

            $response_data['error'] = false;
            $response_data['message'] = 'Login successful.';
            $response_data['user'] = $user;

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else if ($result == USER_NOT_FOUND) {
            $response_data = array();

            $response_data['error'] = true;
            $response_data['message'] = 'User does not exist.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else if ($result == PASSWORD_DO_NOT_MATCH) {
            $response_data = array();

            $response_data['error'] = true;
            $response_data['message'] = 'Invalid credentials.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        }
        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(422);
    }
});

//Gets the therapist
$app->post('/gettherapist', function (Request $request, Response $response) {
    if (!haveEmptyParameters(array('ID'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];

        $db = new DbOperations;

        $therapist = $db->getTherapistByID($ID);

        $response_data = array();

        $response_data['error'] = false;
        $response_data['therapist'] = $therapist;

        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
    }
});

//Gets the profile of the user
$app->post(
    '/getprofile',
    function (Request $request, Response $response) {
        if (!haveEmptyParameters(array('ID'), $request, $response)) {

            $request_data = $request->getParsedBody();

            $ID = $request_data['ID'];

            $db = new DbOperations;

            $profile = $db->getProfile($ID);

            $response_data = array();
            $response_data['error'] = false;
            $response_data['profile'] = $profile;

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        }
    }
);

//Gets all therapists
$app->get('/getalltherapists', function (Request $request, Response $response) {

    $db = new DbOperations;

    $therapists = $db->getAllTherapists();

    $response_data = array();

    $response_data['error'] = false;
    $response_data['therapists'] = $therapists;

    $response->write(json_encode($response_data));

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200);
});

//Gets all entries associated with that ID
$app->post('/getentries', function (Request $request, Response $response) {
    if (!haveEmptyParameters(array('ID'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];

        $db = new DbOperations;

        $entries = $db->getEntriesByID($ID);

        $response_data = array();

        $response_data['error'] = false;
        $response_data['entries'] = $entries;

        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
    }
});

//Gets the full entry (does not keep all the text in the memory and only loads it when needed)
$app->post('/getfullentry', function (Request $request, Response $response) {
    if (!haveEmptyParameters(array('ID', 'title'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];
        $title = $request_data['title'];

        $db = new DbOperations;

        $text = $db->getFullEntry($ID, $title);

        if ($text != null) {
            $response_data = array();

            $response_data['error'] = false;
            $response_data['text'] = $text;

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else {
            $response_data['error'] = true;
            $response_data['text'] = $text;

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(202);
        }
    }
});

//Creates a new chat
$app->post(
    '/addchat',
    function (Request $request, Response $response) {
        if (!haveEmptyParameters(array('UserID', 'TherapistID'), $request, $response)) {

            $request_data = $request->getParsedBody();

            $UserID = $request_data['UserID'];
            $TherapistID = $request_data['TherapistID'];

            $db = new DbOperations;

            $result = $db->createChat($UserID, $TherapistID);

            if ($result == true) {

                $Chat_ID = $db->getChatID($UserID, $TherapistID);
                $message = "";
                $db->sendMessage($UserID, $Chat_ID, $message);

                $response_data = array();
                $response_data['error'] = false;
                $response_data['message'] = 'Chat created successfully.';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(200);
            } else {
                $response_data = array();
                $response_data['error'] = true;
                $response_data['message'] = 'Something went wrong. Please try again later.';

                $response->write(json_encode($response_data));

                return $response
                    ->withHeader('Content-type', 'application/json')
                    ->withStatus(200);
            }
        }
    }
);

//Gets the chat
$app->post('/getchats', function (Request $request, Response $response) {
    if (!haveEmptyParameters(array('ID'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];

        $db = new DbOperations;

        $chats = $db->getChats($ID);

        $response_data = array();

        $response_data['error'] = false;
        $response_data['chats'] = $chats;

        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
    }
});

//Adds an entry
$app->post('/addentry', function (Request $request, Response $response) {
    if (!haveEmptyParameters(array('ID', 'title', 'text'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $ID = $request_data['ID'];
        $title = $request_data['title'];
        $text = $request_data['text'];

        $db = new DbOperations;

        $result = $db->createEntry($ID, $title, $text);

        if ($result == true) {
            $response_data = array();
            $response_data['error'] = false;
            $response_data['message'] = 'Entry added successfully.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else {
            $response_data = array();
            $response_data['error'] = true;
            $response_data['message'] = 'Something went wrong. Please try again later.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        }
    }
});

//Updates the profile
$app->post('/updateprofile', function (Request $request, Response $response, array $args) {

    $request_data = $request->getParsedBody();

    $ID = $request_data['ID'];
    $about = $request_data['about'];
    $image = (isset($request_data['image'])) ? $request_data['image'] : '';
    $path = "../uploads/$ID.jpg";

    $db = new DbOperations;

    if ($db->PhotoExist($ID) == 1) {
        if ($image != null) {
            file_put_contents($path, base64_decode($image));
        }

        $Image_ID = $db->getUserImageByID($ID);

        if ($Image_ID != null) {
            $db->updateImageID($Image_ID, $ID);
        }

        $db->updateProfileAbout($ID, $about);

        $response_data = array();
        $response_data['error'] = false;
        $response_data['message'] = 'User updated successfully.';

        $response->write(json_encode($response_data));

        return $response
            ->withHeader('Content-type', 'application/json')
            ->withStatus(200);
    } else {
        if ($db->profileUpdate($ID, $path)) {
            if ($image != null) {
                file_put_contents($path, base64_decode($image));
            }


            $Image_ID = $db->getUserImageByID($ID);

            if ($Image_ID != null) {
                $db->updateImageID($Image_ID, $ID);
            }

            $db->updateProfileAbout($ID, $about);


            $response_data = array();
            $response_data['error'] = false;
            $response_data['message'] = 'User updated successfully.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else {
            $response_data = array();
            $response_data['error'] = true;
            $response_data['message'] = 'User was not updated.';

            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        }
    }
});

//Updates the password
$app->put('/updatepassword', function (Request $request, Response $response) {

    if (!haveEmptyParameters(array('currentPassword', 'newPassword', 'username'), $request, $response)) {

        $request_data = $request->getParsedBody();

        $currentPassword = $request_data['currentPassword'];
        $newPassword = $request_data['newPassword'];
        $username = $request_data['username'];

        $db = new DbOperations();

        $result = $db->updatePassword($currentPassword, $newPassword, $username);

        if ($result == PASSWORD_CHANGED) {
            $response_data = array();
            $response_data['error'] = false;
            $response_data['message'] = 'Password Changed';
            $response->write(json_encode($response_data));
            return $response->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else if ($result == PASSWORD_DO_NOT_MATCH) {
            $response_data = array();
            $response_data['error'] = true;
            $response_data['message'] = 'You have provided a wrong password.';
            $response->write(json_encode($response_data));
            return $response->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        } else if ($result == PASSWORD_NOT_CHANGED) {
            $response_data = array();
            $response_data['error'] = true;
            $response_data['message'] = 'Some error occurred.';
            $response->write(json_encode($response_data));
            return $response->withHeader('Content-type', 'application/json')
                ->withStatus(200);
        }
    }
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);
});

//Deletes the account
$app->delete('/deleteuser/{ID}', function (Request $request, Response $response, array $args) {
    $ID = $args['ID'];
    $db = new DbOperations;
    $response_data = array();
    if ($db->deleteUser($ID)) {
        $response_data['error'] = false;
        $response_data['message'] = 'User has been deleted';
    } else {
        $response_data['error'] = true;
        $response_data['message'] = 'Plase try again later';
    }
    $response->write(json_encode($response_data));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200);
});

//Checks if it has empty parameters
function haveEmptyParameters($required_params, $request, $response)
{
    $error = false;
    $error_params = '';
    $request_params = $request->getParsedBody();

    //For each parameter it checks if it's not empty or incorrect
    foreach ($required_params as $param) {
        if (!isset($request_params[$param]) || strlen($request_params[$param]) <= 0) {
            $error = true;
            $error_params .= $param . ', ';
        }
    }
    //If any of the parameters are empty or missing, displays an error message
    if ($error) {
        $error_detail = array();
        $error_detail['error'] = true;
        $error_detail['message'] = 'Required parameters ' . substr($error_params, 0, -2) . ' are missing or empty';
        $response->write(json_encode($error_detail));
    }
    return $error;
}

//Runs the application
$app->run();
