import numpy as np


def update_elo (p1, p2, result, k) :
    expectation = 1./(1.+10.**((p2-p1)/400.))

    p1_updated = p1 + k*(result-expectation)
    p2_updated = p2 + k*(expectation-result)

    return p1_updated,p2_updated

def update_elo_teams(t1, t2, result, k) :
    '''
    Update ELO numbers of the team players by using the
    arithmetic mean of their individual ELO numbers.
    '''
    p1 = np.mean(t1)
    p2 = np.mean(t2)
    
    p1_updated, p2_updated = update_elo(p1,p2,result,k)
    t1_updated = np.array(t1)+p1_updated-p1
    t2_updated = np.array(t2)+p2_updated-p2
    return tuple(t1_updated), tuple(t2_updated)
    
    

def update_elo_teams2(t1, t2, result, k) :
    '''
    Update ELO numbers of the team players by using the
    geometric mean of their individual ELO numbers.
    '''

    p1 = np.sqrt(np.prod(t1))
    p2 = np.sqrt(np.prod(t2))
    
    p1_updated, p2_updated = update_elo(p1,p2,result,k)
    t1_updated = np.array(t1)+0.5*(p1_updated-p1)
    t2_updated = np.array(t2)+0.5*(p2_updated-p2)
    return tuple(t1_updated), tuple(t2_updated)


def update_elo_teams3(t1, t2, result, k) :
    '''
    Update ELO numbers of the team players by using the
    geometric mean of their individual ELO numbers.
    '''

    p1 = np.sqrt(np.prod(t1))
    p2 = np.sqrt(np.prod(t2))
    
    p1_updated, p2_updated = update_elo(p1,p2,result,k)
    t1_updated = np.array(t1)+(p1_updated-p1)*np.array([t1[0]/np.sum(t1),t1[1]/np.sum(t1)])
    t2_updated = np.array(t2)+(p2_updated-p2)*np.array([t2[0]/np.sum(t2),t2[1]/np.sum(t2)])

    return tuple(t1_updated), tuple(t2_updated)
