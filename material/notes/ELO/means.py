import numpy as np
import pylab as pl
execfile('elo.py')

x = np.arange(400.,2400.,10.)
y = np.ones_like(x)*1400.
z = np.vstack((x,y))

## arithmetic mean
a = np.mean(z,axis=0)
g = np.sqrt(np.prod(z,axis=0))

fig1 = pl.figure(1)
pl.plot(a,g,'.')
pl.plot(a,a)

pl.xlabel('Geometric mean')
pl.ylabel('Arithmetic mean')



pl.savefig('means.pdf')
