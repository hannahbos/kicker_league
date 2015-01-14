# import numpy as np
# import matplotlib.pyplot as plt

import h5py_wrapper.wrapper as h5w

LEAGUE = 'season15'

data = h5w.load_h5('./%s.h5'%(LEAGUE))
print data
