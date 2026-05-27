# Architecture summary

This Spring Boot application follows a layered architecture with a mixed web interface strategy: MVC controllers render Thymeleaf views for dashboard-style screens, while REST controllers expose JSON APIs for operational modules such as patients, doctors, appointments, and prescriptions. Static frontend pages and JavaScript clients consume those APIs for interactive workflows outside the server-rendered dashboard views.

On the backend, all incoming requests are funneled through controller classes into a service layer that owns business logic, validation flow, and cross-module coordination. Services delegate persistence work to repository interfaces. The data layer is split across two databases: MySQL for relational clinical entities (admin, patient, doctor, appointment) and MongoDB for prescription documents. This keeps transactional relational data in JPA-backed tables and prescription records in a document-oriented store.

# Numbered flow of data and control

1. A user opens a dashboard page or triggers an action from the frontend (form submit, button click, or API call).
2. Spring routes the request to either an MVC controller (for Thymeleaf views) or a REST controller (for JSON endpoints).
3. The controller validates request shape and forwards the request to the appropriate service class.
4. The service executes business rules, coordinates module-level logic, and chooses the correct persistence path.
5. For relational data, the service calls JPA repositories backed by MySQL; for prescription data, it calls the MongoDB repository.
6. The repository layer reads/writes the database and returns persisted results to the service, which prepares the response payload or view model.
7. The controller returns either a rendered HTML template or a REST response, and the frontend updates the dashboard/UI accordingly.
