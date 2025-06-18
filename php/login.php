<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

function respond($success, $extra = []) {
    echo json_encode(array_merge(['success' => $success], $extra));
    exit;
}

$mysqli = new mysqli('localhost', 'Xodiez016', '1pUQN3Vut', 'Xodiez016_usuarios');
if ($mysqli->connect_errno) {
    respond(false, ['message' => 'DB connection error']);
}

$name = $_POST['name'] ?? '';
$password = $_POST['password'] ?? '';
if (!$name || !$password) {
    respond(false, ['message' => 'Missing parameters']);
}

$stmt = $mysqli->prepare('SELECT name,email,password,photo FROM users WHERE name=?');
if (!$stmt) {
    respond(false, ['message' => 'Query error']);
}
$stmt->bind_param('s', $name);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if ($row && password_verify($password, $row['password'])) {
    $photo = $row['photo'];
        if ($photo !== null && $photo !== '') {
            $photo = base64_encode($photo);
        } else {
            $photo = '';
        }
    respond(true, [
        'name' => $row['name'],
        'email' => $row['email'],
        'photo' => $photo
    ]);
} else {
    respond(false, ['message' => 'Invalid credentials']);
}

?>