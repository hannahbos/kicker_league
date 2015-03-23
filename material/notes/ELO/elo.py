import numpy as np


def distribute_points(t,delta, dist_method='equal') :
    if dist_method=='equal' :
        return np.array(t)+0.5*delta
    elif dist_method=='weighted' :
        return np.array(t)+delta*np.array([t[0]/np.sum(t),t[1]/np.sum(t)])


def team_strength(t, method = 'geometric') :
    if method == 'geometric' :
        return np.sqrt(np.prod(t))
    elif method == 'arithmetic' :
        return np.mean(t)
        
    

def update_elo (p0, p1, goals0, goals1, k=20., scale=400., threshold=0.75, result_method='heaviside') :
    expectation = 1./(1.+10.**((p1-p0)/scale))

    if result_method=='heaviside' :
        result = int(goals0 > goals1)

    elif result_method=='threshold_linear' :
        if goals0 > goals1 :
            result = threshold + (1.-threshold)*(1/6.*abs((goals0-goals1)))
        else :
            result = (1.-threshold)*(1.-1/6.*abs((goals0-goals1)))

    p0_updated = p0 + k*(result-expectation)
    p1_updated = p1 + k*(expectation-result)

    return p0_updated,p1_updated

def update_elo_teams(t0, t1, goals0, goals1, k, scale, threshold=0.75, team_method='geometric', result_method='heaviside', dist_method='equal') :
    '''
    Update ELO numbers of the team players by using the
    arithmetic mean of their individual ELO numbers.
    '''
    p0 = team_strength(t0, method=team_method)
    p1 = team_strength(t1, method=team_method)
    
    p0_updated, p1_updated = update_elo(p0,p1,goals0, goals1, k, scale, threshold, result_method)
    t0_updated = distribute_points(t0, p0_updated-p0, dist_method=dist_method)
    t1_updated = distribute_points(t1, p1_updated-p1, dist_method=dist_method)
    return tuple(t0_updated), tuple(t1_updated)
    
