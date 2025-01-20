# config.py
from dataclasses import dataclass
from typing import Optional
from os import environ

@dataclass
class Config:
    DEBUG: bool = environ.get('DEBUG', 'False').lower() == 'true'
    HOST: str = environ.get('HOST', '0.0.0.0')
    PORT: int = int(environ.get('PORT', 5000))
    TIMEOUT: int = int(environ.get('TIMEOUT', 10))
    USER_AGENT: str = environ.get('USER_AGENT', 'Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Safari/537.36')