# Implementaci-n-protocolo-de-envio-de-mensajes
Desarrollar una implementación MSP en la que los clientes se conecten al servidor utilizando un nombre de usuario (uso de password es opcional). 
 El protocolo que se debe seguir es el siguiente:
CONNECT <nombre_usuario><CR/LF> : El cliente envía este comando para solicitar el servicios MSP. El servidor debe mantener abierta la conexión para que el usuario pueda recibir mensajes de otros usuarios. 
CONNECT Juan
DISCONNECT <nombre_usuario><CR/LF>: El cliente envía este comando para cerrar la conexión y dejar de recibir mensajes de otros usuarios. 
SEND #<mensaje>@<nombre_usuario> : Envía un mensaje a un usuario conectado al servidor. El mensaje debe tener como máximo 140 caracteres. Todo lo que esté entre el carácter '#' y el carácter '@' es parte del mensaje incluso los espacios en blanco.
SEND #Hola Juan! Cómo estas?@Juan
LIST: El servidor debe regresar una lista de los usuarios conectados al servidor. 
