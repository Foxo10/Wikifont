<?php
header('Content-Type: application/json');

$DB_SERVER = "localhost";
$DB_USER = "Xodiez016";
$DB_PASS = "1pUQN3Vut";
$DB_DATABASE = "Xodiez016_usuarios";

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
if (!$con) {
    echo json_encode(['success' => false, 'error' => 'Error de conexión']);
    exit();
}

$data = json_decode(file_get_contents('php://input'), true);
$username = $data['username'];
$password = $data['password'];
$email = $data['email'];
$foto = $data['foto_perfil']; // Puede ser "" si el usuario no sube foto

// Comprobar si el usuario o el email ya existe
$stmt = $con->prepare("SELECT id FROM usuarios WHERE username = ? OR email = ?");
$stmt->bind_param("ss", $username, $email);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows > 0) {
    echo json_encode(['success' => false, 'error' => 'Usuario o email ya existe']);
    exit();
}
$hash = password_hash($password, PASSWORD_DEFAULT);

$stmt = $con->prepare("INSERT INTO usuarios (username, password, email, foto_perfil) VALUES (?, ?, ?, ?)");
$stmt->bind_param("ssss", $username, $hash, $email, $foto);

if ($stmt->execute()) {
    echo json_encode(['success' => true]);
} else {
    echo json_encode(['success' => false, 'error' => 'Error al registrar']);
}
?>
