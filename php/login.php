<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

function respond($success, $extra = []) {
    echo json_encode(array_merge(['success' => $success], $extra));
    exit;
}

// DATOS DE LA BBDD
$DB_SERVER   = "localhost";
$DB_USER     = "Xodiez016";
$DB_PASS     = "1pUQN3Vut";
$DB_DATABASE = "Xodiez016_usuarios";

// CONEXIÓN
$mysqli = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
if ($mysqli->connect_errno) {
    respond(false, ['message' => 'Database connection error']);
}

// RECOGER DATOS
$identifier = $_POST['name'] ?? '';
$password   = $_POST['password'] ?? '';
if (!$identifier || !$password) {
    respond(false, ['message' => 'Missing parameters']);
}

// CONSULTA: buscar por nombre o email
$stmt = $mysqli->prepare('SELECT id, name, email, password, photo FROM users WHERE name=? OR email=?');
if (!$stmt) {
    respond(false, ['message' => 'Database query error']);
}
$stmt->bind_param('ss', $identifier, $identifier);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

if ($user && password_verify($password, $user['password'])) {
    // Login OK
    unset($user['password']); // Nunca devuelvas la contraseña
    respond(true, ['user' => $user]);
} else {
    // Login incorrecto
    respond(false, ['message' => 'Invalid username/email or password']);
}
?>
