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

$stmt = $con->prepare("SELECT id, password, email, foto_perfil FROM usuarios WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows == 0) {
    echo json_encode(['success' => false, 'error' => 'Usuario no encontrado']);
    exit();
}

$stmt->bind_result($id, $hash, $email, $foto_perfil);
$stmt->fetch();

if (password_verify($password, $hash)) {
    echo json_encode([
        'success' => true,
        'id' => $id,
        'username' => $username,
        'email' => $email,
        'foto_perfil' => $foto_perfil
    ]);
} else {
    echo json_encode(['success' => false, 'error' => 'Contraseña incorrecta']);
}
?>
