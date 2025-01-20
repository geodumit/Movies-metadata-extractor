from dataclasses import dataclass
from typing import Optional

@dataclass
class Rating:
    rating: Optional[str] = None
    rating_count: Optional[str] = None
    error: Optional[str] = None