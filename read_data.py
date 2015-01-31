# import numpy as np
# import matplotlib.pyplot as plt

import core

LEAGUE = 'test'

name, started, data = core.load_data(LEAGUE, create_new=False)
print data
