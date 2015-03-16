import numpy as np
import pylab as pl
execfile('elo.py')

x = np.arange(1400.,2400.,10.)
y = np.ones_like(x)*1400.
z = np.vstack((x,y))



t1_updated_arith = []
t1_updated_geo = []
t1_updated_geo_2 = []
t1_updated_geo2 = []
t1_updated_geo2_2 = []
t2_updated = []

result=1.
k=20.
for t1 in zip(x,y) :
    t2 = (1400.,1400.)

    d1_arith,d2_arith = update_elo_teams(t1, t2, result, k) ### arithmetic
    t1_updated_arith.append(d1_arith[1]-t1[1])

    d1_geo,d2_geo = update_elo_teams2(t1, t2, result, k) ### geometric
    t1_updated_geo.append(d1_geo[1]-t1[1])
    t1_updated_geo_2.append(d1_geo[0]-t1[0])

    d1_geo2,d2_geo2 = update_elo_teams3(t1, t2, result, k) ### geometric
    t1_updated_geo2.append(d1_geo2[1]-t1[1])
    t1_updated_geo2_2.append(d1_geo2[0]-t1[0])


    

fig= pl.figure()
ax = fig.add_subplot(211)
pl.title('Team1 wins')
pl.plot(x,t1_updated_geo, label='equal dist. P1')
pl.plot(x,t1_updated_geo_2, label='equal dist. P0')
#pl.plot(x,np.array(t1_updated_geo)+np.array(t1_updated_geo_2), 'x',label='geo mean sum')

pl.plot(x,t1_updated_geo2, label='weighted dist. P1')
pl.plot(x,t1_updated_geo2_2, label='weighted dist. P0 (strong)')
#pl.plot(x,np.array(t1_updated_geo2)+np.array(t1_updated_geo2_2), 'x',label='geo2 mean sum')
pl.ylabel(r'$\delta^{(j)}$')
pl.legend(loc='upper right')


t1_updated_arith = []
t1_updated_geo = []
t1_updated_geo_2 = []
t1_updated_geo2 = []
t1_updated_geo2_2 = []
t2_updated = []


result=0.
k=20.
for t1 in zip(x,y) :
    t2 = (1400.,1400.)

    d1_arith,d2_arith = update_elo_teams(t1, t2, result, k) ### arithmetic
    t1_updated_arith.append(d1_arith[1]-t1[1])

    d1_geo,d2_geo = update_elo_teams2(t1, t2, result, k) ### geometric
    t1_updated_geo.append(d1_geo[1]-t1[1])
    t1_updated_geo_2.append(d1_geo[0]-t1[0])

    d1_geo2,d2_geo2 = update_elo_teams3(t1, t2, result, k) ### geometric
    t1_updated_geo2.append(d1_geo2[1]-t1[1])
    t1_updated_geo2_2.append(d1_geo2[0]-t1[0])





ax = fig.add_subplot(212)
pl.title('Team1 loses')
pl.plot(x,t1_updated_geo, label='geo mean P1')
pl.plot(x,t1_updated_geo_2, label='geo mean P0  (strong)')
#pl.plot(x,np.array(t1_updated_geo)+np.array(t1_updated_geo_2), 'x',label='geo mean sum')

pl.plot(x,t1_updated_geo2, label='geo2 mean P1')
pl.plot(x,t1_updated_geo2_2, label='geo2 mean P0  (strong)')
#pl.plot(x,np.array(t1_updated_geo2)+np.array(t1_updated_geo2_2), 'x',label='geo2 mean sum')
pl.ylabel(r'$\delta^{(j)}$')
pl.xlabel('P0 strength')
pl.savefig('test_elo.pdf')
