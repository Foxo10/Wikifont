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
$name     = $_POST['name']     ?? '';
$email    = $_POST['email']    ?? '';
$password = $_POST['password'] ?? '';
$photo    = $_POST['photo']    ?? 'uploads/default.png';

if (!$name || !$email || !$password) {
    respond(false, ['message' => 'Missing parameters']);
}

// Validar email
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    respond(false, ['message' => 'Invalid email format']);
}

// ¿Ya existe ese usuario/email?
$stmt = $mysqli->prepare('SELECT id FROM users WHERE name=? OR email=?');
$stmt->bind_param('ss', $name, $email);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows > 0) {
    respond(false, ['message' => 'Username or email already exists']);
}

// Hashear la contraseña
$hash = password_hash($password, PASSWORD_DEFAULT);

// Insertar usuario
$stmt = $mysqli->prepare('INSERT INTO users (name, email, password, photo) VALUES (?, ?, ?, ?)');
$stmt->bind_param('ssss', $name, $email, $hash, $photo);
if ($stmt->execute()) {
    respond(true, ['message' => 'User registered successfully']);
} else {
    respond(false, ['message' => 'Registration error']);
}
?>
