-- create department table
CREATE TABLE if not exists department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(100) NOT NULL,
    annual_budget NUMERIC(15,2) NOT NULL
);
-- insert some records
INSERT INTO department (name, location, annual_budget)
VALUES
    ('Engineering', 'New York', 1000000.00),
    ('Marketing', 'Los Angeles', 500000.00),
    ('HR', 'Chicago', 300000.00),
    ('Finance', 'Boston', 400000.00)
    ON CONFLICT (name) DO NOTHING;

-- create employee table add association
CREATE TABLE if not exists employee (
                          id SERIAL PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          title VARCHAR(50) NOT NULL,
                          hire_date DATE NOT NULL,
                          salary NUMERIC(15,2) NOT NULL,
                          department_id INT NOT NULL,
                          CONSTRAINT fk_department
                              FOREIGN KEY(department_id)
                                  REFERENCES department(id)
                                  ON DELETE RESTRICT
);


-- insert sample data with relationship.
INSERT INTO employee (full_name, email, title, hire_date, salary, department_id)
VALUES
    ('Alice Johnson', 'alice.johnson@company.com', 'Engineer', '2022-01-15', 90000.00,
     (SELECT id FROM department WHERE name='Engineering')),
    ('Bob Smith', 'bob.smith@company.com', 'Senior Engineer', '2021-06-10', 120000.00,
     (SELECT id FROM department WHERE name='Engineering')),
    ('Clara Lee', 'clara.lee@company.com', 'Marketing Specialist', '2023-03-20', 60000.00,
     (SELECT id FROM department WHERE name='Marketing')),
    ('David Brown', 'david.brown@company.com', 'HR Manager', '2020-11-01', 80000.00,
     (SELECT id FROM department WHERE name='HR'))
ON CONFLICT (email) DO NOTHING;

-- create project table
CREATE TABLE if not exists project (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL UNIQUE ,
                         description TEXT,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,
                         budget NUMERIC(15,2) NOT NULL,
                         status VARCHAR(50) NOT NULL
);

INSERT INTO project (name, description, start_date, end_date, budget, status)
VALUES
    ('Website Revamp', 'Redesign the corporate website.', '2024-01-01', '2024-06-30', 150000.00, 'Active'),
    ('Mobile App', 'Develop a new mobile application.', '2024-02-15', '2024-09-30', 200000.00, 'Active'),
    ('Marketing Campaign', 'Launch Q3 marketing campaign.', '2024-03-01', '2024-08-31', 50000.00, 'Planned')
ON CONFLICT (name) DO NOTHING;
-- select * from project;

-- create client
CREATE TABLE if not exists client (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        industry VARCHAR(100),
                        contact_person VARCHAR(100),
                        contact_phone VARCHAR(20),
                        contact_email VARCHAR(100) NOT NULL UNIQUE
);
INSERT INTO client (name, industry, contact_person, contact_phone, contact_email)
VALUES
    ('Acme Corp', 'Manufacturing', 'John Doe', '555-1234', 'johndoe@acme.com'),
    ('BetaTech', 'Technology', 'Jane Smith', '555-5678', 'janesmith@betatech.com'),
    ('GreenFoods', 'Food & Beverage', 'Mike Green', '555-9012', 'mike.green@greenfoods.com')
ON CONFLICT (contact_email) DO NOTHING;

-- create project allocation
CREATE TABLE IF NOT EXISTS project_allocation (
                                    employee_id INT NOT NULL,
                                    project_id INT NOT NULL,
                                    allocation_percentage NUMERIC(5,2) NOT NULL,
                                    PRIMARY KEY (employee_id, project_id),
                                    CONSTRAINT fk_employee
                                        FOREIGN KEY (employee_id)
                                            REFERENCES employee(id)
                                            ON DELETE CASCADE,
                                    CONSTRAINT fk_project
                                        FOREIGN KEY (project_id)
                                            REFERENCES project(id)
                                            ON DELETE CASCADE
);

INSERT INTO project_allocation (employee_id, project_id, allocation_percentage)
VALUES
    ((SELECT id FROM employee WHERE email='alice.johnson@company.com'),
     (SELECT id FROM project WHERE name='Website Revamp'), 50.00),

    ((SELECT id FROM employee WHERE email='alice.johnson@company.com'),
     (SELECT id FROM project WHERE name='Mobile App'), 50.00),

    ((SELECT id FROM employee WHERE email='bob.smith@company.com'),
     (SELECT id FROM project WHERE name='Website Revamp'), 100.00)
ON CONFLICT (employee_id, project_id) DO NOTHING;

-- project table
CREATE TABLE IF NOT EXISTS project_client (
                                project_id INT NOT NULL,
                                client_id INT NOT NULL,
                                PRIMARY KEY (project_id, client_id),
                                CONSTRAINT fk_project
                                    FOREIGN KEY (project_id)
                                        REFERENCES project(id)
                                        ON DELETE CASCADE,
                                CONSTRAINT fk_client
                                    FOREIGN KEY (client_id)
                                        REFERENCES client(id)
                                        ON DELETE CASCADE
);
INSERT INTO project_client (project_id, client_id)
VALUES
    ((SELECT id FROM project WHERE name='Website Revamp'),
     (SELECT id FROM client WHERE contact_email='johndoe@acme.com')),

    ((SELECT id FROM project WHERE name='Website Revamp'),
     (SELECT id FROM client WHERE contact_email='janesmith@betatech.com')),

    ((SELECT id FROM project WHERE name='Mobile App'),
     (SELECT id FROM client WHERE contact_email='janesmith@betatech.com'))
ON CONFLICT (project_id, client_id) DO NOTHING;
-- select * from project_client;












