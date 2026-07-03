import json
from main import Base, AgencyDB, VehicleDB, FavoriteDB, NotificationDB, ChatDB, engine, SessionLocal

def populate():
    db = SessionLocal()
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)

    # 1. Agências
    db.add(AgencyDB(id="1", name="Premium Auto", cnpj="12.345/0001", imageUrl="https://placehold.co/100x100?text=Premium", latitude=-23.55, longitude=-46.63, rating=5.0, reviewCount=50))

    # 2. Veículos
    db.add(VehicleDB(
        id="v1", agencyId="1", brand="Porsche", model="911 Carrera", version="992",
        year=2024, mileage=0, price=950000.0, transmission="PDK", fuel="Gasolina",
        imageUrlsJson=json.dumps(["https://images.unsplash.com/photo-1503376780353-7e6692767b70"]),
        description="O ícone esportivo.", location="São Paulo", rating=5.0, reviewCount=12,
        specsJson=json.dumps({"0-100": "3.2s"}), color="Cinza"
    ))

    # 3. Notificações
    db.add(NotificationDB(id="n1", title="Preço Baixou!", message="O Tesla que você favoritou caiu 5%!", time="10 min atrás"))

    # 4. Chats (Simulados para o usuário ID '1' se ele existir)
    db.add(ChatDB(
        id="c1", userId="1", userName="João Vendedor", userPhoto=None,
        vehicleName="Porsche 911", vehiclePhoto="https://placehold.co/100x100?text=911",
        lastMessage="Olá! O carro ainda está disponível?", time="14:20", unreadCount=1
    ))

    db.commit()
    print("Banco de dados populado com sucesso!")
    db.close()

if __name__ == "__main__":
    populate()
