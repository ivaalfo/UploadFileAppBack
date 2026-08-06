Intrucciones para su instalacion:

- Bajarse el proyecto en C:\desarrollo\git\Martico\fileTransfer

o similar

- Sustituir las apariciones de KCNA17-008Node04 en el fichero 
fileTransferEAR\EarContent\META-INF\ibmconfig\cells\defaultCell\applications\defaultApp\deployments\defaultApp\deployment.xml

por el nombre del equipo donde se descarga el proyecto

- Hay que tener definidas dos shared libraries en el websphere, en la consola de websphere:

Environment -> Shared Libraries: 

Martico-SharedLib y Martico-FileTransfer-SharedLib