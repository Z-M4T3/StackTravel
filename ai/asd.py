from fastapi import FastAPI
import uvicorn

from langchain_groq import ChatGroq
from dotenv import load_dotenv
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from pydantic import BaseModel, Field

load_dotenv()  # Betölti a .env fájlból az API kulcsot

class Card(BaseModel):
    name: str = Field(description="A program megnevezése pár szóban")
    description: str = Field(description="Egy mondatos bemutatás, leírás")
    category: str = Field(description="Kategorizáld egy szóval a programot (pl: étterem, látványosság, koncert, művészet, kocsma, múzeum stb.)")
    location: str = Field(description="Google Maps link, ahol a program található")
    webpage: str | None = Field(default=None, description="A programot nyújtó szolgáltató hivatalos weboldala vagy közösségi média oldala")
    duration: str = Field(description="A program időtartamának becsült ideje")

class Activites(BaseModel):
    activities: list[Card] = Field(description="Egymástól különböző programok")

def generate_cards(destination, category, count) -> Activites:
    model = ChatGroq(model="openai/gpt-oss-120b", temperature=0.5)
    structured = model.with_structured_output(Activites)

    if category != None and category != "":
        system_message = f'''Utazási programajánló vagy. A megadott úticélhoz pontosan
                    {count} különböző {category} katergóriájú programot ajánlj).
                    Csak valóban létező helyeket adj meg. Ha nem vagy biztos valamiben,
                    hagyd null-on, ne találj ki adatot. Magyarul válaszolj.'''
    else:
        system_message = f'''Utazási programajánló vagy. A megadott úticélhoz pontosan
                            {count} különböző programot ajánlj (pl. látnivalók, gasztronómia, koncertek, múzeumok,
                            helyi nevezetességek, kocsmák, különlegességek stb.).
                            Csak valóban létező helyeket adj meg. Ha nem vagy biztos valamiben,
                            hagyd null-on, ne találj ki adatot. Magyarul válaszolj.'''

    messages = [
        SystemMessage(content=(system_message)),
        HumanMessage(content=f"Úticél: {destination}"),
    ]
    return structured.invoke(messages)


app = FastAPI()

@app.get("/generate/")
def destination(destination: str, count: int, category: str | None = None):
    result = generate_cards(destination, category, count)
    cards = []
    for card in result.activities:
        cards.append(card.model_dump())
    return cards


if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8090)
