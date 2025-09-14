from graphviz import Digraph

# Create a UML class diagram using Graphviz
dot = Digraph("BankingApplication", filename="banking_app_uml", format="png")

dot.attr(rankdir="LR", size="10")

# Entities
dot.node("User", """{User|
- id: Long\\l
- firstName: String\\l
- lastName: String\\l
- middleName: String\\l
- gender: String\\l
- dateOfBirth: LocalDate\\l
- address: String\\l
- email: String\\l
- phoneNumber: String\\l
- accountNumber: String\\l
- password: String\\l}""", shape="record")

dot.node("Transaction", """{Transaction|
- id: Long\\l
- accountNumber: String\\l
- amount: BigDecimal\\l
- transactionType: String\\l
- status: String\\l
- timeStamp: LocalDateTime\\l}""", shape="record")

# DTOs
dot.node("AccountInfo", "{AccountInfo}", shape="record")
dot.node("BankResponse", "{BankResponse}", shape="record")
dot.node("LoginRequest", "{LoginRequest}", shape="record")
dot.node("UserRequest", "{UserRequest}", shape="record")

# Controllers
dot.node("UserController", "{UserController}", shape="record")
dot.node("TransactionController", "{TransactionController}", shape="record")

# Services
dot.node("UserService", "{<<interface>>\\nUserService}", shape="record")
dot.node("UserServiceImpl", "{UserServiceImpl}", shape="record")
dot.node("TransactionService", "{<<interface>>\\nTransactionService}", shape="record")
dot.node("TransactionServiceImpl", "{TransactionServiceImpl}", shape="record")

# Repositories
dot.node("UserRepository", "{<<interface>>\\nUserRepository}", shape="record")
dot.node("TransactionRepository", "{<<interface>>\\nTransactionRepository}", shape="record")

# Utils
dot.node("AccountUtils", "{AccountUtils}", shape="record")

# Config
dot.node("SecurityConfig", "{SecurityConfig}", shape="record")

# Relationships
dot.edges([("UserRepository", "User"), ("TransactionRepository", "Transaction")])
dot.edges([("UserServiceImpl", "UserRepository"), ("UserServiceImpl", "AccountUtils")])
dot.edge("UserServiceImpl", "UserService", arrowhead="onormal", label="implements")
dot.edge("TransactionServiceImpl", "TransactionService", arrowhead="onormal", label="implements")
dot.edge("TransactionServiceImpl", "TransactionRepository")
dot.edge("TransactionServiceImpl", "UserRepository")
dot.edge("UserController", "UserService")
dot.edge("TransactionController", "TransactionService")
dot.edge("SecurityConfig", "UserServiceImpl", style="dashed", label="provides BCryptPasswordEncoder")

# DTO usage
dot.edge("UserServiceImpl", "UserRequest", style="dashed")
dot.edge("UserServiceImpl", "LoginRequest", style="dashed")
dot.edge("UserServiceImpl", "BankResponse", style="dashed")
dot.edge("TransactionServiceImpl", "BankResponse", style="dashed")
dot.edge("TransactionServiceImpl", "Transaction", style="dashed")
dot.edge("TransactionServiceImpl", "AccountInfo", style="dashed")

# Render the diagram
file_path = "/mnt/data/banking_app_uml.png"
dot.render(file_path, view=False)

file_path
