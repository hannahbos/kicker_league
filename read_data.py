# import numpy as np
# import matplotlib.pyplot as plt

import sys

import core

league = sys.argv[1]

name, started, data = core.load_data(league, create_new=False)
print data
