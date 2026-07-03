from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
from sqlalchemy import create_engine, Column, Integer, String, Float, ForeignKey, Text, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from passlib.context import CryptContext
import json
import uvicorn

# 1. Configuração do Banco de Dados
DATABASE_URL = "sqlite:///./carwatch_local.db"
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# 2. Segurança
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# 3. Modelos do Banco de Dados (SQLAlchemy)
class UserDB(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    email = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=False)

class AgencyDB(Base):
    __tablename__ = "agencies"
    id = Column(String, primary_key=True)
    name = Column(String)
    cnpj = Column(String)
    imageUrl = Column(String)
    latitude = Column(Float)
    longitude = Column(Float)
    rating = Column(Float)
    reviewCount = Column(Integer)

class VehicleDB(Base):
    __tablename__ = "vehicles"
    id = Column(String, primary_key=True)
    agencyId = Column(String, ForeignKey("agencies.id"))
    brand = Column(String)
    model = Column(String)
    version = Column(String)
    year = Column(Integer)
    mileage = Column(Integer)
    price = Column(Float)
    originalPrice = Column(Float, nullable=True)
    transmission = Column(String)
    fuel = Column(String)
    imageUrlsJson = Column(Text)
    description = Column(Text)
    location = Column(String)
    rating = Column(Float)
    reviewCount = Column(Integer)
    statusTag = Column(String, nullable=True)
    specsJson = Column(Text)
    color = Column(String)

class FavoriteDB(Base):
    __tablename__ = "favorites"
    id = Column(Integer, primary_key=True, index=True)
    userId = Column(String, index=True)
    vehicleId = Column(String, index=True)

class NotificationDB(Base):
    __tablename__ = "notifications"
    id = Column(String, primary_key=True)
    title = Column(String)
    message = Column(String)
    time = Column(String)

class ChatDB(Base):
    __tablename__ = "chats"
    id = Column(String, primary_key=True)
    userId = Column(String, index=True)
    userName = Column(String)
    userPhoto = Column(String, nullable=True)
    vehicleName = Column(String)
    vehiclePhoto = Column(String, nullable=True)
    lastMessage = Column(String)
    time = Column(String)
    unreadCount = Column(Integer)

Base.metadata.create_all(bind=engine)

# 4. Schemas de Dados (Pydantic)
class UserResponse(BaseModel):
    id: str
    name: str
    email: str
    message: str

class UserLoginRequest(BaseModel):
    email: EmailStr
    password: str

class UserRegisterRequest(BaseModel):
    name: str
    email: EmailStr
    password: str

# 5. Inicialização da API
app = FastAPI(title="CarWatch Pro Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def get_db():
    db = SessionLocal()
    try: yield db
    finally: db.close()

# 6. Rotas de Autenticação
@app.post("/api/auth/register", response_model=UserResponse)
def register(user_data: UserRegisterRequest, db: Session = Depends(get_db)):
    db_user = db.query(UserDB).filter(UserDB.email == user_data.email).first()
    if db_user: raise HTTPException(400, "E-mail já cadastrado.")
    hashed = pwd_context.hash(user_data.password)
    new_user = UserDB(name=user_data.name, email=user_data.email, hashed_password=hashed)
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return {"id": str(new_user.id), "name": new_user.name, "email": new_user.email, "message": "OK"}

@app.post("/api/auth/login", response_model=UserResponse)
def login(creds: UserLoginRequest, db: Session = Depends(get_db)):
    user = db.query(UserDB).filter(UserDB.email == creds.email).first()
    if not user or not pwd_context.verify(creds.password, user.hashed_password):
        raise HTTPException(401, "Credenciais inválidas")
    return {"id": str(user.id), "name": user.name, "email": user.email, "message": "OK"}

# 7. Rotas de Veículos e Agências
def format_vehicle(v, user_id, db):
    is_fav = False
    if user_id:
        is_fav = db.query(FavoriteDB).filter(FavoriteDB.userId == user_id, FavoriteDB.vehicleId == v.id).first() is not None
    return {
        "id": v.id, "agencyId": v.agencyId, "brand": v.brand, "model": v.model,
        "version": v.version, "year": v.year, "mileage": v.mileage, "price": v.price,
        "originalPrice": v.originalPrice, "transmission": v.transmission, "fuel": v.fuel,
        "imageUrlsList": json.loads(v.imageUrlsJson),
        "description": v.description, "location": v.location, "rating": v.rating,
        "reviewCount": v.reviewCount, "statusTag": v.statusTag,
        "specs": json.loads(v.specsJson), "color": v.color, "isFavorite": is_fav
    }

@app.get("/api/vehicles/featured")
def get_featured(userId: str = None, db: Session = Depends(get_db)):
    vehicles = db.query(VehicleDB).all()
    return [format_vehicle(v, userId, db) for v in vehicles]

@app.post("/api/vehicles")
def post_vehicle(vehicle: dict, db: Session = Depends(get_db)):
    new_v = VehicleDB(
        id=vehicle['id'], agencyId=vehicle['agencyId'], brand=vehicle['brand'],
        model=vehicle['model'], version=vehicle['version'], year=vehicle['year'],
        mileage=vehicle['mileage'], price=vehicle['price'], transmission=vehicle['transmission'],
        fuel=vehicle['fuel'], imageUrlsJson=json.dumps(vehicle['imageUrlsList']),
        description=vehicle['description'], location=vehicle['location'],
        rating=4.0, reviewCount=0, specsJson=json.dumps(vehicle['specs']), color=vehicle['color']
    )
    db.add(new_v)
    db.commit()
    return vehicle

@app.get("/api/notifications")
def get_notifications(db: Session = Depends(get_db)):
    return db.query(NotificationDB).all()

@app.get("/api/chats")
def get_chats(userId: str, db: Session = Depends(get_db)):
    return db.query(ChatDB).filter(ChatDB.userId == userId).all()

@app.post("/api/vehicles/favorite/{id}")
def toggle_fav(id: str, userId: str, db: Session = Depends(get_db)):
    fav = db.query(FavoriteDB).filter(FavoriteDB.userId == userId, FavoriteDB.vehicleId == id).first()
    if fav: db.delete(fav)
    else: db.add(FavoriteDB(userId=userId, vehicleId=id))
    db.commit()
    return {"status": "success"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
