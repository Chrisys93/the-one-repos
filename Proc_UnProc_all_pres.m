% Make a function out of this, that takes the officeIoT folder address and
% the number of runs as variables;

clear

OPB1 = dlmread('one-comp/officeIoT/RPBWR1', ' ', 0, 1);
PPB1 = dlmread('one-comp/non-proc_proc/RPBWR1', ' ', 0, 1);


OPB2 = dlmread('one-comp/officeIoT/RPBWR2', ' ', 0, 1);
PPB2 = dlmread('one-comp/non-proc_proc/RPBWR2', ' ', 0, 1);


OPB3 = dlmread('one-comp/officeIoT/RPBWR3', ' ', 0, 1);
PPB3 = dlmread('one-comp/non-proc_proc/RPBWR3', ' ', 0, 1);


OPB4 = dlmread('one-comp/officeIoT/RPBWR4', ' ', 0, 1);
PPB4 = dlmread('one-comp/non-proc_proc/RPBWR4', ' ', 0, 1);


OPB5 = dlmread('one-comp/officeIoT/RPBWR5', ' ', 0, 1);
PPB5 = dlmread('one-comp/non-proc_proc/RPBWR5', ' ', 0, 1);


OPB6 = dlmread('one-comp/officeIoT/RPBWR6', ' ', 0, 1);
PPB6 = dlmread('one-comp/non-proc_proc/RPBWR6', ' ', 0, 1);

OUB1 = dlmread('one-comp/officeIoT/RUPBWR1', ' ', 0, 1);
PUB1 = dlmread('one-comp/non-proc_proc/RUPBWR1', ' ', 0, 1);


OUB2 = dlmread('one-comp/officeIoT/RUPBWR2', ' ', 0, 1);
PUB2 = dlmread('one-comp/non-proc_proc/RUPBWR2', ' ', 0, 1);


OUB3 = dlmread('one-comp/officeIoT/RUPBWR3', ' ', 0, 1);
PUB3 = dlmread('one-comp/non-proc_proc/RUPBWR3', ' ', 0, 1);


OUB4 = dlmread('one-comp/officeIoT/RUPBWR4', ' ', 0, 1);
PUB4 = dlmread('one-comp/non-proc_proc/RUPBWR4', ' ', 0, 1);


OUB5 = dlmread('one-comp/officeIoT/RUPBWR5', ' ', 0, 1);
PUB5 = dlmread('one-comp/non-proc_proc/RUPBWR5', ' ', 0, 1);


OUB6 = dlmread('one-comp/officeIoT/RUPBWR6', ' ', 0, 1);
PUB6 = dlmread('one-comp/non-proc_proc/RUPBWR6', ' ', 0, 1);

COPB1 = dlmread('one-cloud/officeIoT/RPBWR1', ' ', 0, 1);
CPPB1 = dlmread('one-cloud/non-proc_proc/RPBWR1', ' ', 0, 1);


COPB2 = dlmread('one-cloud/officeIoT/RPBWR2', ' ', 0, 1);
CPPB2 = dlmread('one-cloud/non-proc_proc/RPBWR2', ' ', 0, 1);


COPB3 = dlmread('one-cloud/officeIoT/RPBWR3', ' ', 0, 1);
CPPB3 = dlmread('one-cloud/non-proc_proc/RPBWR3', ' ', 0, 1);


COPB4 = dlmread('one-cloud/officeIoT/RPBWR4', ' ', 0, 1);
CPPB4 = dlmread('one-cloud/non-proc_proc/RPBWR4', ' ', 0, 1);


COPB5 = dlmread('one-cloud/officeIoT/RPBWR5', ' ', 0, 1);
CPPB5 = dlmread('one-cloud/non-proc_proc/RPBWR5', ' ', 0, 1);


COPB6 = dlmread('one-cloud/officeIoT/RPBWR6', ' ', 0, 1);
CPPB6 = dlmread('one-cloud/non-proc_proc/RPBWR6', ' ', 0, 1);

COUB1 = dlmread('one-cloud/officeIoT/RUPBWR1', ' ', 0, 1);
CPUB1 = dlmread('one-cloud/non-proc_proc/RUPBWR1', ' ', 0, 1);


COUB2 = dlmread('one-cloud/officeIoT/RUPBWR2', ' ', 0, 1);
CPUB2 = dlmread('one-cloud/non-proc_proc/RUPBWR2', ' ', 0, 1);


COUB3 = dlmread('one-cloud/officeIoT/RUPBWR3', ' ', 0, 1);
CPUB3 = dlmread('one-cloud/non-proc_proc/RUPBWR3', ' ', 0, 1);


COUB4 = dlmread('one-cloud/officeIoT/RUPBWR4', ' ', 0, 1);
CPUB4 = dlmread('one-cloud/non-proc_proc/RUPBWR4', ' ', 0, 1);


COUB5 = dlmread('one-cloud/officeIoT/RUPBWR5', ' ', 0, 1);
CPUB5 = dlmread('one-cloud/non-proc_proc/RUPBWR5', ' ', 0, 1);


COUB6 = dlmread('one-cloud/officeIoT/RUPBWR6', ' ', 0, 1);
CPUB6 = dlmread('one-cloud/non-proc_proc/RUPBWR6', ' ', 0, 1);


[r3, c3] = size(OPB1);

for repo = 1:c3
    oproc_up(repo, 1) = mean(OPB1(:, repo))/(mean(OUB1(:, repo))+mean(OPB1(:, repo)));
    if (isnan(mean(OPB1(:, repo))/(mean(OUB1(:, repo))+mean(OPB1(:, repo)))))
        oproc_up(repo, 1) = 0;
    end
    oproc_up(repo, 2) = mean(OPB2(:, repo))/(mean(OUB2(:, repo))+mean(OPB2(:, repo)));
    if (isnan(mean(OPB2(:, repo))/(mean(OUB2(:, repo))+mean(OPB2(:, repo)))))
        oproc_up(repo, 2) = 0;
    end
    oproc_up(repo, 3) = mean(OPB3(:, repo))/(mean(OUB3(:, repo))+mean(OPB3(:, repo)));
    if (isnan(mean(OPB3(:, repo))/(mean(OUB3(:, repo))+mean(OPB3(:, repo)))))
        oproc_up(repo, 3) = 0;
    end
    oproc_up(repo, 4) = mean(OPB4(:, repo))/(mean(OUB4(:, repo))+mean(OPB4(:, repo)));
    if (isnan(mean(OPB4(:, repo))/(mean(OUB4(:, repo))+mean(OPB4(:, repo)))))
        oproc_up(repo, 4) = 0;
    end
    oproc_up(repo, 5) = mean(OPB5(:, repo))/(mean(OUB5(:, repo))+mean(OPB5(:, repo)));
    if (isnan(mean(OPB5(:, repo))/(mean(OUB5(:, repo))+mean(OPB5(:, repo)))))
        oproc_up(repo, 5) = 0;
    end
    oproc_up(repo, 6) =  mean(OPB6(:, repo))/(mean(OUB6(:, repo))+mean(OPB6(:, repo)));
    if (isnan(mean(OPB6(:, repo))/(mean(OUB6(:, repo))+mean(OPB6(:, repo)))))
        oproc_up(repo, 6) = 0;
    end
    pproc_up(repo, 1) = mean(PPB1(:, repo))/(mean(PUB1(:, repo))+mean(PPB1(:, repo)));
    if (isnan(mean(PPB1(:, repo))/(mean(PUB1(:, repo))+mean(PPB1(:, repo)))))
        pproc_up(repo, 1) = 0;
    end
    pproc_up(repo, 2) = mean(PPB2(:, repo))/(mean(PUB2(:, repo))+mean(PPB2(:, repo)));
    if (isnan(mean(PPB2(:, repo))/(mean(PUB2(:, repo))+mean(PPB2(:, repo)))))
        pproc_up(repo, 2) = 0;
    end
    pproc_up(repo, 3) = mean(PPB3(:, repo))/(mean(PUB3(:, repo))+mean(PPB3(:, repo)));
    if (isnan(mean(PPB3(:, repo))/(mean(PUB3(:, repo))+mean(PPB3(:, repo)))))
        pproc_up(repo, 3) = 0;
    end
    pproc_up(repo, 4) = mean(PPB4(:, repo))/(mean(PUB4(:, repo))+mean(PPB4(:, repo)));
    if (isnan(mean(PPB4(:, repo))/(mean(PUB4(:, repo))+mean(PPB4(:, repo)))))
        pproc_up(repo, 4) = 0;
    end
    pproc_up(repo, 5) = mean(PPB5(:, repo))/(mean(PUB5(:, repo))+mean(PPB5(:, repo)));
    if (isnan(mean(PPB5(:, repo))/(mean(PUB5(:, repo))+mean(PPB5(:, repo)))))
        pproc_up(repo, 5) = 0;
    end
    pproc_up(repo, 6) =  mean(PPB6(:, repo))/(mean(PUB6(:, repo))+mean(PPB6(:, repo)));
    if (isnan(mean(PPB6(:, repo))/(mean(PUB6(:, repo))+mean(PPB6(:, repo)))))
        pproc_up(repo, 6) = 0;
    end
    
    
    ouproc_up(repo, 1) = mean(OUB1(:, repo))/(mean(OUB1(:, repo))+mean(OPB1(:, repo)));
    if (isnan(mean(OUB1(:, repo))/(mean(OUB1(:, repo))+mean(OPB1(:, repo)))))
        ouproc_up(repo, 1) = 0;
    end
    ouproc_up(repo, 2) = mean(OUB2(:, repo))/(mean(OUB2(:, repo))+mean(OPB2(:, repo)));
    if (isnan(mean(OUB2(:, repo))/(mean(OUB2(:, repo))+mean(OPB2(:, repo)))))
        ouproc_up(repo, 2) = 0;
    end
    ouproc_up(repo, 3) = mean(OUB3(:, repo))/(mean(OUB3(:, repo))+mean(OPB3(:, repo)));
    if (isnan(mean(OUB3(:, repo))/(mean(OUB3(:, repo))+mean(OPB3(:, repo)))))
        ouproc_up(repo, 3) = 0;
    end
    ouproc_up(repo, 4) = mean(OUB4(:, repo))/(mean(OUB4(:, repo))+mean(OPB4(:, repo)));
    if (isnan(mean(OUB4(:, repo))/(mean(OUB4(:, repo))+mean(OPB4(:, repo)))))
        ouproc_up(repo, 4) = 0;
    end
    ouproc_up(repo, 5) = mean(OUB5(:, repo))/(mean(OUB5(:, repo))+mean(OPB5(:, repo)));
    if (isnan(mean(OUB5(:, repo))/(mean(OUB5(:, repo))+mean(OPB5(:, repo)))))
        ouproc_up(repo, 5) = 0;
    end
    ouproc_up(repo, 6) =  mean(OUB6(:, repo))/(mean(OUB6(:, repo))+mean(OPB6(:, repo)));
    if (isnan(mean(OUB6(:, repo))/(mean(OUB6(:, repo))+mean(OPB6(:, repo)))))
        ouproc_up(repo, 6) = 0;
    end
    puproc_up(repo, 1) = mean(PUB1(:, repo))/(mean(PUB1(:, repo))+mean(PPB1(:, repo)));
    if (isnan(mean(PUB1(:, repo))/(mean(PUB1(:, repo))+mean(PPB1(:, repo)))))
        puproc_up(repo, 1) = 0;
    end
    puproc_up(repo, 2) = mean(PUB2(:, repo))/(mean(PUB2(:, repo))+mean(PPB2(:, repo)));
    if (isnan(mean(PUB2(:, repo))/(mean(PUB2(:, repo))+mean(PPB2(:, repo)))))
        puproc_up(repo, 2) = 0;
    end
    puproc_up(repo, 3) = mean(PUB3(:, repo))/(mean(PUB3(:, repo))+mean(PPB3(:, repo)));
    if (isnan(mean(PUB3(:, repo))/(mean(PUB3(:, repo))+mean(PPB3(:, repo)))))
        puproc_up(repo, 3) = 0;
    end
    puproc_up(repo, 4) = mean(PUB4(:, repo))/(mean(PUB4(:, repo))+mean(PPB4(:, repo)));
    if (isnan(mean(PUB4(:, repo))/(mean(PUB4(:, repo))+mean(PPB4(:, repo)))))
        puproc_up(repo, 4) = 0;
    end
    puproc_up(repo, 5) = mean(PUB5(:, repo))/(mean(PUB5(:, repo))+mean(PPB5(:, repo)));
    if (isnan(mean(PUB5(:, repo))/(mean(PUB5(:, repo))+mean(PPB5(:, repo)))))
        puproc_up(repo, 5) = 0;
    end
    puproc_up(repo, 6) =  mean(PUB6(:, repo))/(mean(PUB6(:, repo))+mean(PPB6(:, repo)));
    if (isnan(mean(PUB6(:, repo))/(mean(PUB6(:, repo))+mean(PPB6(:, repo)))))
        puproc_up(repo, 6) = 0;
    end
    
    
    coproc_up(repo, 1) = mean(COPB1(:, repo))/(mean(COUB1(:, repo))+mean(COPB1(:, repo)));
    if (isnan(mean(COPB1(:, repo))/(mean(COUB1(:, repo))+mean(COPB1(:, repo)))))
        coproc_up(repo, 1) = 0;
    end
    coproc_up(repo, 2) = mean(COPB2(:, repo))/(mean(COUB2(:, repo))+mean(COPB2(:, repo)));
    if (isnan(mean(COPB2(:, repo))/(mean(COUB2(:, repo))+mean(COPB2(:, repo)))))
       coproc_up(repo, 2) = 0;
    end
    coproc_up(repo, 3) = mean(COPB3(:, repo))/(mean(COUB3(:, repo))+mean(COPB3(:, repo)));
    if (isnan(mean(COPB3(:, repo))/(mean(COUB3(:, repo))+mean(COPB3(:, repo)))))
        coproc_up(repo, 3) = 0;
    end
    coproc_up(repo, 4) = mean(COPB4(:, repo))/(mean(COUB4(:, repo))+mean(COPB4(:, repo)));
    if (isnan(mean(COPB4(:, repo))/(mean(COUB4(:, repo))+mean(COPB4(:, repo)))))
        coproc_up(repo, 4) = 0;
    end
    coproc_up(repo, 5) = mean(COPB5(:, repo))/(mean(COUB5(:, repo))+mean(COPB5(:, repo)));
    if (isnan(mean(COPB5(:, repo))/(mean(COUB5(:, repo))+mean(COPB5(:, repo)))))
        coproc_up(repo, 5) = 0;
    end
    coproc_up(repo, 6) =  mean(COPB6(:, repo))/(mean(COUB6(:, repo))+mean(COPB6(:, repo)));
    if (isnan(mean(COPB6(:, repo))/(mean(COUB6(:, repo))+mean(COPB6(:, repo)))))
        coproc_up(repo, 6) = 0;
    end
    cpproc_up(repo, 1) = mean(CPPB1(:, repo))/(mean(CPUB1(:, repo))+mean(CPPB1(:, repo)));
    if (isnan(mean(CPPB1(:, repo))/(mean(CPUB1(:, repo))+mean(CPPB1(:, repo)))))
        cpproc_up(repo, 1) = 0;
    end
    cpproc_up(repo, 2) = mean(CPPB2(:, repo))/(mean(CPUB2(:, repo))+mean(CPPB2(:, repo)));
    if (isnan(mean(CPPB2(:, repo))/(mean(CPUB2(:, repo))+mean(CPPB2(:, repo)))))
        cpproc_up(repo, 2) = 0;
    end
    cpproc_up(repo, 3) = mean(CPPB3(:, repo))/(mean(CPUB3(:, repo))+mean(CPPB3(:, repo)));
    if (isnan(mean(CPPB3(:, repo))/(mean(CPUB3(:, repo))+mean(CPPB3(:, repo)))))
        cpproc_up(repo, 3) = 0;
    end
    cpproc_up(repo, 4) = mean(CPPB4(:, repo))/(mean(CPUB4(:, repo))+mean(CPPB4(:, repo)));
    if (isnan(mean(CPPB4(:, repo))/(mean(CPUB4(:, repo))+mean(CPPB4(:, repo)))))
        cpproc_up(repo, 4) = 0;
    end
    cpproc_up(repo, 5) = mean(CPPB5(:, repo))/(mean(CPUB5(:, repo))+mean(CPPB5(:, repo)));
    if (isnan(mean(CPPB5(:, repo))/(mean(CPUB5(:, repo))+mean(CPPB5(:, repo)))))
        cpproc_up(repo, 5) = 0;
    end
    cpproc_up(repo, 6) =  mean(CPPB6(:, repo))/(mean(CPUB6(:, repo))+mean(CPPB6(:, repo)));
    if (isnan(mean(CPPB6(:, repo))/(mean(CPUB6(:, repo))+mean(CPPB6(:, repo)))))
        cpproc_up(repo, 6) = 0;
    end
    
    
    couproc_up(repo, 1) = mean(COUB1(:, repo))/(mean(COUB1(:, repo))+mean(COPB1(:, repo)));
    if (isnan(mean(COUB1(:, repo))/(mean(COUB1(:, repo))+mean(COPB1(:, repo)))))
        couproc_up(repo, 1) = 0;
    end
    couproc_up(repo, 2) = mean(COUB2(:, repo))/(mean(COUB2(:, repo))+mean(COPB2(:, repo)));
    if (isnan(mean(COUB2(:, repo))/(mean(COUB2(:, repo))+mean(COPB2(:, repo)))))
        couproc_up(repo, 2) = 0;
    end
    couproc_up(repo, 3) = mean(COUB3(:, repo))/(mean(COUB3(:, repo))+mean(COPB3(:, repo)));
    if (isnan(mean(COUB3(:, repo))/(mean(COUB3(:, repo))+mean(COPB3(:, repo)))))
        couproc_up(repo, 3) = 0;
    end
    couproc_up(repo, 4) = mean(COUB4(:, repo))/(mean(COUB4(:, repo))+mean(COPB4(:, repo)));
    if (isnan(mean(COUB4(:, repo))/(mean(COUB4(:, repo))+mean(COPB4(:, repo)))))
        couproc_up(repo, 4) = 0;
    end
    couproc_up(repo, 5) = mean(COUB5(:, repo))/(mean(COUB5(:, repo))+mean(COPB5(:, repo)));
    if (isnan(mean(COUB5(:, repo))/(mean(COUB5(:, repo))+mean(COPB5(:, repo)))))
        couproc_up(repo, 5) = 0;
    end
    couproc_up(repo, 6) =  mean(COUB6(:, repo))/(mean(COUB6(:, repo))+mean(COPB6(:, repo)));
    if (isnan(mean(COUB6(:, repo))/(mean(COUB6(:, repo))+mean(COPB6(:, repo)))))
        couproc_up(repo, 6) = 0;
    end
    cpuproc_up(repo, 1) = mean(CPUB1(:, repo))/(mean(CPUB1(:, repo))+mean(CPPB1(:, repo)));
    if (isnan(mean(CPUB1(:, repo))/(mean(CPUB1(:, repo))+mean(CPPB1(:, repo)))))
        cpuproc_up(repo, 1) = 0;
    end
    cpuproc_up(repo, 2) = mean(CPUB2(:, repo))/(mean(CPUB2(:, repo))+mean(CPPB2(:, repo)));
    if (isnan(mean(CPUB2(:, repo))/(mean(CPUB2(:, repo))+mean(CPPB2(:, repo)))))
        cpuproc_up(repo, 2) = 0;
    end
    cpuproc_up(repo, 3) = mean(CPUB3(:, repo))/(mean(CPUB3(:, repo))+mean(CPPB3(:, repo)));
    if (isnan(mean(CPUB3(:, repo))/(mean(CPUB3(:, repo))+mean(CPPB3(:, repo)))))
        cpuproc_up(repo, 3) = 0;
    end
    cpuproc_up(repo, 4) = mean(CPUB4(:, repo))/(mean(CPUB4(:, repo))+mean(CPPB4(:, repo)));
    if (isnan(mean(CPUB4(:, repo))/(mean(CPUB4(:, repo))+mean(CPPB4(:, repo)))))
        cpuproc_up(repo, 4) = 0;
    end
    cpuproc_up(repo, 5) = mean(CPUB5(:, repo))/(mean(CPUB5(:, repo))+mean(CPPB5(:, repo)));
    if (isnan(mean(CPUB5(:, repo))/(mean(CPUB5(:, repo))+mean(CPPB5(:, repo)))))
        cpuproc_up(repo, 5) = 0;
    end
    cpuproc_up(repo, 6) =  mean(CPUB6(:, repo))/(mean(CPUB6(:, repo))+mean(CPPB6(:, repo)));
    if (isnan(mean(CPUB6(:, repo))/(mean(CPUB6(:, repo))+mean(CPPB6(:, repo)))))
        cpuproc_up(repo, 6) = 0;
    end
    
    
    
    
    oproc(repo, 1) = mean(OPB1(:, repo));
    if (isnan(mean(OPB1(:, repo))))
        oproc(repo, 1) = 0;
    end
    oproc(repo, 2) = mean(OPB2(:, repo));
    if (isnan(mean(OPB2(:, repo))))
        oproc(repo, 2) = 0;
    end
    oproc(repo, 3) = mean(OPB3(:, repo));
    if (isnan(mean(OPB3(:, repo))))
        oproc(repo, 3) = 0;
    end
    oproc(repo, 4) = mean(OPB4(:, repo));
    if (isnan(mean(OPB4(:, repo))))
        oproc(repo, 4) = 0;
    end
    oproc(repo, 5) = mean(OPB5(:, repo));
    if (isnan(mean(OPB5(:, repo))))
        oproc(repo, 5) = 0;
    end
    oproc(repo, 6) =  mean(OPB6(:, repo));
    if (isnan(mean(OPB6(:, repo))))
        oproc(repo, 6) = 0;
    end
    pproc(repo, 1) = mean(PPB1(:, repo));
    if (isnan(mean(PPB1(:, repo))))
        pproc(repo, 1) = 0;
    end
    pproc(repo, 2) = mean(PPB2(:, repo));
    if (isnan(mean(PPB2(:, repo))))
        pproc(repo, 2) = 0;
    end
    pproc(repo, 3) = mean(PPB3(:, repo));
    if (isnan(mean(PPB3(:, repo))))
        pproc(repo, 3) = 0;
    end
    pproc(repo, 4) = mean(PPB4(:, repo));
    if (isnan(mean(PPB4(:, repo))))
        pproc(repo, 4) = 0;
    end
    pproc(repo, 5) = mean(PPB5(:, repo));
    if (isnan(mean(PPB5(:, repo))))
        pproc(repo, 5) = 0;
    end
    pproc(repo, 6) =  mean(PPB6(:, repo));
    if (isnan(mean(PPB6(:, repo))))
        pproc(repo, 6) = 0;
    end
    ouproc(repo, 1) = mean(OUB1(:, repo));
    if (isnan(mean(OUB1(:, repo))))
        ouproc(repo, 1) = 0;
    end
    ouproc(repo, 2) = mean(OUB2(:, repo));
    if (isnan(mean(OUB2(:, repo))))
        ouproc(repo, 2) = 0;
    end
    ouproc(repo, 3) = mean(OUB3(:, repo));
    if (isnan(mean(OUB3(:, repo))))
        ouproc(repo, 3) = 0;
    end
    ouproc(repo, 4) = mean(OUB4(:, repo));
    if (isnan(mean(OUB4(:, repo))))
        ouproc(repo, 4) = 0;
    end
    ouproc(repo, 5) = mean(OUB5(:, repo));
    if (isnan(mean(OUB5(:, repo))))
        ouproc(repo, 5) = 0;
    end
    ouproc(repo, 6) =  mean(OUB6(:, repo));
    if (isnan(mean(OUB6(:, repo))))
        ouproc(repo, 6) = 0;
    end
    puproc(repo, 1) = mean(PUB1(:, repo));
    if (isnan(mean(PUB1(:, repo))))
        puproc(repo, 1) = 0;
    end
    puproc(repo, 2) = mean(PUB2(:, repo));
    if (isnan(mean(PUB2(:, repo))))
        puproc(repo, 2) = 0;
    end
    puproc(repo, 3) = mean(PUB3(:, repo));
    if (isnan(mean(PUB3(:, repo))))
        puproc(repo, 3) = 0;
    end
    puproc(repo, 4) = mean(PUB4(:, repo));
    if (isnan(mean(PUB4(:, repo))))
        puproc(repo, 4) = 0;
    end
    puproc(repo, 5) = mean(PUB5(:, repo));
    if (isnan(mean(PUB5(:, repo))))
        puproc(repo, 5) = 0;
    end
    puproc(repo, 6) =  mean(PUB6(:, repo));
    if (isnan(mean(PUB6(:, repo))))
        puproc(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([oproc_up(:,1), oproc_up(:,2), oproc_up(:,3), oproc_up(:,4), oproc_up(:,5), oproc_up(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Processed/Total Processing Messages (100%)','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(oproc_up(:,1))), mean(nonzeros(oproc_up(:,2))), mean(nonzeros(oproc_up(:,3))), mean(nonzeros(oproc_up(:,4))), mean(nonzeros(oproc_up(:,5))), mean(nonzeros(oproc_up(:,6)));
    mean(nonzeros(pproc_up(:,1))), mean(nonzeros(pproc_up(:,2))), mean(nonzeros(pproc_up(:,3))), mean(nonzeros(pproc_up(:,4))), mean(nonzeros(pproc_up(:,5))), mean(nonzeros(pproc_up(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Processed/Total Processing Messages (100%)','fontsize',12)
%TSDS:
% Modify this:
% lgd1 =legend('1: f; 1.1; 100; 3:1 \newline2: f; 3; 100; 4:1; \newline3: t; 4; 100; 4:1; \newline4: t; 2; 100; 1:2', ...
%             '1: f; 1.3; 100; 3:1 \newline2: f; 3.2; 100; 4:1; \newline3: t; 2.2; 500; 4:1;  \newline4: t; 2; 500; 2:2', ...
%             '1: f; 2; 100; 3:1 \newline2: f; 50; 100; 4:1; \newline3: f; 3; 100 4:1; \newline4: f; 2; 100; 4:2', ...
%             '1: f; 1.6; 300; 3:1 \newline2: f; 3; 200; 4:1; \newline3: f; 4; 300 4:1; \newline4: f; 2; 300; 5:2', ...
%             '1: f; 2.2; 600; 3:1 \newline2: f; 3.2; 300; 4:1; \newline3: f; 2.2; 600 4:1; \newline4: f; 2; 600; 3:1', ...
%             '1: f; 2; 900; 4:1 \newline2: f; 50; 600; 4:1; \newline3: f; 3; 900 4:1; \newline4: f; 2; 900; 4:1', 'Location', 'southoutside');
% title(lgd1, 'Scenario Number: Storage Mode; Processing Delay; proc Freshness Period; proc Shelf-Life; non-proc Shelf-Life; Compress:Delete Ratio; Non-Proc:Proc Ratio');
% lgd1.NumColumns = 6;
% xlim([17 48]);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])

figure

% subplot(2,1,1);
% yyaxis left
y = [mean(coproc_up(:,1)), mean(couproc_up(:,1)), mean(coproc_up(:,2)), mean(couproc_up(:,2)), mean(coproc_up(:,3)), mean(couproc_up(:,3)), mean(coproc_up(:,4)), mean(couproc_up(:,4)), mean(coproc_up(:,5)), mean(couproc_up(:,5)), mean(coproc_up(:,6)), mean(couproc_up(:,6));
    mean(cpproc_up(:,1)), mean(cpuproc_up(:,1)), mean(cpproc_up(:,2)), mean(cpuproc_up(:,2)), mean(cpproc_up(:,3)), mean(cpuproc_up(:,3)), mean(cpproc_up(:,4)), mean(cpuproc_up(:,4)), mean(cpproc_up(:,5)), mean(cpuproc_up(:,5)), mean(cpproc_up(:,6)), mean(cpuproc_up(:,6));
    mean(oproc_up(:,1)), mean(ouproc_up(:,1)), mean(oproc_up(:,2)), mean(ouproc_up(:,2)), mean(oproc_up(:,3)), mean(ouproc_up(:,3)), mean(oproc_up(:,4)), mean(ouproc_up(:,4)), mean(oproc_up(:,5)), mean(ouproc_up(:,5)), mean(oproc_up(:,6)), mean(ouproc_up(:,6));
    mean(pproc_up(:,1)), mean(puproc_up(:,1)), mean(pproc_up(:,2)), mean(puproc_up(:,2)), mean(pproc_up(:,3)), mean(puproc_up(:,3)), mean(pproc_up(:,4)), mean(puproc_up(:,4)), mean(pproc_up(:,5)), mean(puproc_up(:,5)), mean(pproc_up(:,6)), mean(puproc_up(:,6))];
% title('Processing threads','fontsize',16)
bar(y, 'group')
set(gca,'xtick',1:4,'XTickLabel',{'Office', 'Cars', 'Office', 'Cars'}, 'fontsize', 10)
% xt= [1-0.34, 1-0.2, 1-0.07, 1.07, 1.2, 1.34, 3-0.34, 3-0.2, 3-0.07, 3.07, 3.2, 3.34, 5-0.34, 5-0.2, 5-0.07, 5.07, 5.2, 5.34, 7-0.34, 7-0.2, 7-0.07, 7.07, 7.2, 7.34];
% yt=[y(1,:), y(3,:), y(5,:), y(7,:)]+50;
%xt=[1, 3, 5, 7]-0.25;
%yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% ytxt=num2str([y(1, :)'; y(3, :)'; y(5, :)'; y(7, :)'],'%.1f');
% text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% title('Processing threads','fontsize',16)
% xlabel('Scenario Number','fontsize',12) 
ylabel('Satisfied Processing Message Upload (100%)','fontsize',12)

groupX = [2 4]; %// central value of each group
groupY = -0.1; %// vertical position of texts. Adjust as needed
deltaY = .03; %// controls vertical compression of axis. Adjust as needed
groupNames = {'IP', 'EDR'}; %// note different lengths to test centering
for g = 1:numel(groupX)
    h = text(groupX(g), groupY, groupNames(g), 'Fontsize', 13, 'Fontweight', 'bold');
        %// create text for group with appropriate font size and weight
    pos = get(h, 'Position');
    ext = get(h, 'Extent');
    pos(1) = pos(1) - ext(3)/2; %// horizontally correct position to make it centered
    set(h, 'Position', pos); %// set corrected position for text
end
pos = get(gca, 'Position');
pos(2) = pos(2) + deltaY; %// vertically compress axis to make room for texts
set(gca, 'Position', pos); %/ set corrected position for axis

% xlabel('Scenario Number','fontsize',12) 
ylabel('Processed/UnProcessed Messages BW (B/s)','fontsize',12)
% ylim([0 15e5]);


OPF1 = dlmread('one-comp/officeIoT/RPFR1', ' ', 0, 1);
PPF1 = dlmread('one-comp/non-proc_proc/RPFR1', ' ', 0, 1);


OPF2 = dlmread('one-comp/officeIoT/RPFR2', ' ', 0, 1);
PPF2 = dlmread('one-comp/non-proc_proc/RPFR2', ' ', 0, 1);


OPF3 = dlmread('one-comp/officeIoT/RPFR3', ' ', 0, 1);
PPF3 = dlmread('one-comp/non-proc_proc/RPFR3', ' ', 0, 1);


OPF4 = dlmread('one-comp/officeIoT/RPFR4', ' ', 0, 1);
PPF4 = dlmread('one-comp/non-proc_proc/RPFR4', ' ', 0, 1);


OPF5 = dlmread('one-comp/officeIoT/RPFR5', ' ', 0, 1);
PPF5 = dlmread('one-comp/non-proc_proc/RPFR5', ' ', 0, 1);


OPF6 = dlmread('one-comp/officeIoT/RPFR6', ' ', 0, 1);
PPF6 = dlmread('one-comp/non-proc_proc/RPFR6', ' ', 0, 1);


COPF1 = dlmread('one-cloud/officeIoT/RPFR1', ' ', 0, 1);
CPPF1 = dlmread('one-cloud/non-proc_proc/RPFR1', ' ', 0, 1);


COPF2 = dlmread('one-cloud/officeIoT/RPFR2', ' ', 0, 1);
CPPF2 = dlmread('one-cloud/non-proc_proc/RPFR2', ' ', 0, 1);


COPF3 = dlmread('one-cloud/officeIoT/RPFR3', ' ', 0, 1);
CPPF3 = dlmread('one-cloud/non-proc_proc/RPFR3', ' ', 0, 1);


COPF4 = dlmread('one-cloud/officeIoT/RPFR4', ' ', 0, 1);
CPPF4 = dlmread('one-cloud/non-proc_proc/RPFR4', ' ', 0, 1);


COPF5 = dlmread('one-cloud/officeIoT/RPFR5', ' ', 0, 1);
CPPF5 = dlmread('one-cloud/non-proc_proc/RPFR5', ' ', 0, 1);


COPF6 = dlmread('one-cloud/officeIoT/RPFR6', ' ', 0, 1);
CPPF6 = dlmread('one-cloud/non-proc_proc/RPFR6', ' ', 0, 1);

[r3, c3] = size(OPF1);

for repo = 1:c3
    ofresh_perc(repo, 1) = OPF1(1, repo)/sum(OPF1(:, repo));
    if (isnan(OPF1(1, repo)/sum(OPF1(:, repo))))
        ofresh_perc(repo, 1) = 0;
    end
    ofresh_perc(repo, 2) = OPF2(1, repo)/sum(OPF2(:, repo));
    if (isnan(OPF2(1, repo)/sum(OPF2(:, repo))))
        ofresh_perc(repo, 2) = 0;
    end
    ofresh_perc(repo, 3) = OPF3(1, repo)/sum(OPF3(:, repo));
    if (isnan(OPF3(1, repo)/sum(OPF3(:, repo))))
        ofresh_perc(repo, 3) = 0;
    end
    ofresh_perc(repo, 4) = OPF4(1, repo)/sum(OPF4(:, repo));
    if (isnan(OPF4(1, repo)/sum(OPF4(:, repo))))
        ofresh_perc(repo, 4) = 0;
    end
    ofresh_perc(repo, 5) = OPF5(1, repo)/sum(OPF5(:, repo));
    if (isnan(OPF5(1, repo)/sum(OPF5(:, repo))))
        ofresh_perc(repo, 5) = 0;
    end
    ofresh_perc(repo, 6) =  OPF6(1, repo)/sum(OPF6(:, repo));
    if (isnan(OPF6(1, repo)/sum(OPF6(:, repo))))
        ofresh_perc(repo, 6) = 0;
    end
    pfresh_perc(repo, 1) = PPF1(1, repo)/sum(PPF1(:, repo));
    if (isnan(PPF1(1, repo)/sum(PPF1(:, repo))))
        pfresh_perc(repo, 1) = 0;
    end
    pfresh_perc(repo, 2) = PPF2(1, repo)/sum(PPF2(:, repo));
    if (isnan(PPF2(1, repo)/sum(PPF2(:, repo))))
        pfresh_perc(repo, 2) = 0;
    end
    pfresh_perc(repo, 3) = PPF3(1, repo)/sum(PPF3(:, repo));
    if (isnan(PPF3(1, repo)/sum(PPF3(:, repo))))
        pfresh_perc(repo, 3) = 0;
    end
    pfresh_perc(repo, 4) = PPF4(1, repo)/sum(PPF4(:, repo));
    if (isnan(PPF4(1, repo)/sum(PPF4(:, repo))))
        pfresh_perc(repo, 4) = 0;
    end
    pfresh_perc(repo, 5) = PPF5(1, repo)/sum(PPF5(:, repo));
    if (isnan(PPF5(1, repo)/sum(PPF5(:, repo))))
        pfresh_perc(repo, 5) = 0;
    end
    pfresh_perc(repo, 6) =  PPF6(1, repo)/sum(PPF6(:, repo));
    if (isnan(PPF6(1, repo)/sum(PPF6(:, repo))))
        pfresh_perc(repo, 6) = 0;
    end
    
    
    cofresh_perc(repo, 1) = COPF1(1, repo)/sum(COPF1(:, repo));
    if (isnan(COPF1(1, repo)/sum(COPF1(:, repo))))
        cofresh_perc(repo, 1) = 0;
    end
    cofresh_perc(repo, 2) = COPF2(1, repo)/sum(COPF2(:, repo));
    if (isnan(COPF2(1, repo)/sum(COPF2(:, repo))))
        cofresh_perc(repo, 2) = 0;
    end
    cofresh_perc(repo, 3) = COPF3(1, repo)/sum(COPF3(:, repo));
    if (isnan(COPF3(1, repo)/sum(COPF3(:, repo))))
        cofresh_perc(repo, 3) = 0;
    end
    cofresh_perc(repo, 4) = COPF4(1, repo)/sum(COPF4(:, repo));
    if (isnan(COPF4(1, repo)/sum(COPF4(:, repo))))
        cofresh_perc(repo, 4) = 0;
    end
    cofresh_perc(repo, 5) = COPF5(1, repo)/sum(COPF5(:, repo));
    if (isnan(COPF5(1, repo)/sum(COPF5(:, repo))))
        cofresh_perc(repo, 5) = 0;
    end
    cofresh_perc(repo, 6) =  COPF6(1, repo)/sum(COPF6(:, repo));
    if (isnan(COPF6(1, repo)/sum(COPF6(:, repo))))
        cofresh_perc(repo, 6) = 0;
    end
    cpfresh_perc(repo, 1) = CPPF1(1, repo)/sum(CPPF1(:, repo));
    if (isnan(CPPF1(1, repo)/sum(CPPF1(:, repo))))
        cpfresh_perc(repo, 1) = 0;
    end
    cpfresh_perc(repo, 2) = CPPF2(1, repo)/sum(CPPF2(:, repo));
    if (isnan(CPPF2(1, repo)/sum(CPPF2(:, repo))))
        cpfresh_perc(repo, 2) = 0;
    end
    cpfresh_perc(repo, 3) = CPPF3(1, repo)/sum(CPPF3(:, repo));
    if (isnan(CPPF3(1, repo)/sum(CPPF3(:, repo))))
        cpfresh_perc(repo, 3) = 0;
    end
    cpfresh_perc(repo, 4) = CPPF4(1, repo)/sum(CPPF4(:, repo));
    if (isnan(CPPF4(1, repo)/sum(CPPF4(:, repo))))
        cpfresh_perc(repo, 4) = 0;
    end
    cpfresh_perc(repo, 5) = CPPF5(1, repo)/sum(CPPF5(:, repo));
    if (isnan(CPPF5(1, repo)/sum(CPPF5(:, repo))))
        cpfresh_perc(repo, 5) = 0;
    end
    cpfresh_perc(repo, 6) =  CPPF6(1, repo)/sum(CPPF6(:, repo));
    if (isnan(CPPF6(1, repo)/sum(CPPF6(:, repo))))
        cpfresh_perc(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([ofresh_perc(:,1), ofresh_perc(:,2), ofresh_perc(:,3), ofresh_perc(:,4), ofresh_perc(:,5), ofresh_perc(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Processing Message Freshness (100%)','fontsize',12)
ylim([0 1]);
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(ofresh_perc(:,1))), mean(nonzeros(ofresh_perc(:,2))), mean(nonzeros(ofresh_perc(:,3))), mean(nonzeros(ofresh_perc(:,4))), mean(nonzeros(ofresh_perc(:,5))), mean(nonzeros(ofresh_perc(:,6)));
    mean(nonzeros(pfresh_perc(:,1))), mean(nonzeros(pfresh_perc(:,2))), mean(nonzeros(pfresh_perc(:,3))), mean(nonzeros(pfresh_perc(:,4))), mean(nonzeros(pfresh_perc(:,5))), mean(nonzeros(pfresh_perc(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Processing Message Freshness (100%)','fontsize',12)
ylim([0 1]);



figure

% yyaxis left
y = [mean(nonzeros(coproc_up(:,1))), mean(nonzeros(coproc_up(:,2))), mean(nonzeros(coproc_up(:,3))), mean(nonzeros(coproc_up(:,4))), mean(nonzeros(coproc_up(:,5))), mean(nonzeros(coproc_up(:,6)));
    mean(nonzeros(cofresh_perc(:,1))), mean(nonzeros(cofresh_perc(:,2))), mean(nonzeros(cofresh_perc(:,3))), mean(nonzeros(cofresh_perc(:,4))), mean(nonzeros(cofresh_perc(:,5))), mean(nonzeros(cofresh_perc(:,6)));
    mean(nonzeros(cpproc_up(:,1))), mean(nonzeros(cpproc_up(:,2))), mean(nonzeros(cpproc_up(:,3))), mean(nonzeros(cpproc_up(:,4))), mean(nonzeros(cpproc_up(:,5))), mean(nonzeros(cpproc_up(:,6)));
    mean(nonzeros(cpfresh_perc(:,1))), mean(nonzeros(cpfresh_perc(:,2))), mean(nonzeros(cpfresh_perc(:,3))), mean(nonzeros(cpfresh_perc(:,4))), mean(nonzeros(cpfresh_perc(:,5))), mean(nonzeros(cpfresh_perc(:,6)))
    mean(nonzeros(oproc_up(:,1))), mean(nonzeros(oproc_up(:,2))), mean(nonzeros(oproc_up(:,3))), mean(nonzeros(oproc_up(:,4))), mean(nonzeros(oproc_up(:,5))), mean(nonzeros(oproc_up(:,6)));
    mean(nonzeros(ofresh_perc(:,1))), mean(nonzeros(ofresh_perc(:,2))), mean(nonzeros(ofresh_perc(:,3))), mean(nonzeros(ofresh_perc(:,4))), mean(nonzeros(ofresh_perc(:,5))), mean(nonzeros(ofresh_perc(:,6)));
    mean(nonzeros(pproc_up(:,1))), mean(nonzeros(pproc_up(:,2))), mean(nonzeros(pproc_up(:,3))), mean(nonzeros(pproc_up(:,4))), mean(nonzeros(pproc_up(:,5))), mean(nonzeros(pproc_up(:,6)))
    mean(nonzeros(pfresh_perc(:,1))), mean(nonzeros(pfresh_perc(:,2))), mean(nonzeros(pfresh_perc(:,3))), mean(nonzeros(pfresh_perc(:,4))), mean(nonzeros(pfresh_perc(:,5))), mean(nonzeros(pfresh_perc(:,6)))];
bar(y,'group')
set(gca,'xtick',1:8,'XTickLabel',{'Processed', 'Fresh', 'Processed', 'Fresh', 'Processed', 'Fresh', 'Processed', 'Fresh'}, 'fontsize', 10)
% xt= [1-0.34, 1-0.2, 1-0.07, 1.07, 1.2, 1.34, 3-0.34, 3-0.2, 3-0.07, 3.07, 3.2, 3.34, 5-0.34, 5-0.2, 5-0.07, 5.07, 5.2, 5.34, 7-0.34, 7-0.2, 7-0.07, 7.07, 7.2, 7.34];
% yt=[y(1,:), y(3,:), y(5,:), y(7,:)]+50;
%xt=[1, 3, 5, 7]-0.25;
%yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% ytxt=num2str([y(1, :)'; y(3, :)'; y(5, :)'; y(7, :)'],'%.1f');
% text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% title('Processing threads','fontsize',16)
% xlabel('Scenario Number','fontsize',12) 
ylabel('Satisfied Processing Message Upload (100%)','fontsize',12)

groupX = [2 4 6 8]; %// central value of each group
groupY = -0.1; %// vertical position of texts. Adjust as needed
deltaY = .03; %// controls vertical compression of axis. Adjust as needed
groupNames = {'Offices IP', 'Cars IP', 'Offices EDR', 'Cars EDR'}; %// note different lengths to test centering
for g = 1:numel(groupX)
    h = text(groupX(g), groupY, groupNames(g), 'Fontsize', 13, 'Fontweight', 'bold');
        %// create text for group with appropriate font size and weight
    pos = get(h, 'Position');
    ext = get(h, 'Extent');
    pos(1) = pos(1) - ext(3)/2; %// horizontally correct position to make it centered
    set(h, 'Position', pos); %// set corrected position for text
end
pos = get(gca, 'Position');
pos(2) = pos(2) + deltaY; %// vertically compress axis to make room for texts
set(gca, 'Position', pos); %/ set corrected position for axis


% yyaxis right
% y = [mean(nonzeros(oproc_up(:,1))), mean(nonzeros(oproc_up(:,2))), mean(nonzeros(oproc_up(:,3))), mean(nonzeros(oproc_up(:,4))), mean(nonzeros(oproc_up(:,5))), mean(nonzeros(oproc_up(:,6)));
%     mean(nonzeros(ofresh_perc(:,1))), mean(nonzeros(ofresh_perc(:,2))), mean(nonzeros(ofresh_perc(:,3))), mean(nonzeros(ofresh_perc(:,4))), mean(nonzeros(ofresh_perc(:,5))), mean(nonzeros(ofresh_perc(:,6)));
%     mean(nonzeros(hproc_up(:,1))), mean(nonzeros(hproc_up(:,2))), mean(nonzeros(hproc_up(:,3))), mean(nonzeros(hproc_up(:,4))), mean(nonzeros(hproc_up(:,5))), mean(nonzeros(hproc_up(:,6)));
%     mean(nonzeros(hfresh_perc(:,1))), mean(nonzeros(hfresh_perc(:,2))), mean(nonzeros(hfresh_perc(:,3))), mean(nonzeros(hfresh_perc(:,4))), mean(nonzeros(hfresh_perc(:,5))), mean(nonzeros(hfresh_perc(:,6)));
%     mean(nonzeros(bproc_up(:,1))), mean(nonzeros(bproc_up(:,2))), mean(nonzeros(bproc_up(:,3))), mean(nonzeros(bproc_up(:,4))), mean(nonzeros(bproc_up(:,5))), mean(nonzeros(bproc_up(:,6)));
%     mean(nonzeros(bfresh_perc(:,1))), mean(nonzeros(bfresh_perc(:,2))), mean(nonzeros(bfresh_perc(:,3))), mean(nonzeros(bfresh_perc(:,4))), mean(nonzeros(bfresh_perc(:,5))), mean(nonzeros(bfresh_perc(:,6)));
%     mean(nonzeros(pproc_up(:,1))), mean(nonzeros(pproc_up(:,2))), mean(nonzeros(pproc_up(:,3))), mean(nonzeros(pproc_up(:,4))), mean(nonzeros(pproc_up(:,5))), mean(nonzeros(pproc_up(:,6)))
%     mean(nonzeros(pfresh_perc(:,1))), mean(nonzeros(pfresh_perc(:,2))), mean(nonzeros(pfresh_perc(:,3))), mean(nonzeros(pfresh_perc(:,4))), mean(nonzeros(pfresh_perc(:,5))), mean(nonzeros(pfresh_perc(:,6)))];
% bar([1:8],[0,0,0,0,0,0; y(2, :); 0,0,0,0,0,0; y(4, :); 0,0,0,0,0,0; y(6, :); 0,0,0,0,0,0; y(8, :)],'group')
% % set(gca,'xtick',1:8)
% % xt= [2-0.34, 2-0.2, 2-0.07, 2.07, 2.2, 2.34, 4-0.34, 4-0.2, 4-0.07, 4.07, 4.2, 4.34, 6-0.34, 6-0.2, 6-0.07, 6.07, 6.2, 6.34, 8-0.34, 8-0.2, 8-0.07, 8.07, 8.2, 8.34];
% % yt=[y(2,:), y(4,:), y(6,:), y(8,:)]-50;
% %xt=[1, 3, 5, 7]-0.25;
% %yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% % ytxt=num2str([y(2, :)'; y(4, :)'; y(6, :)'; y(8, :)'],'%.1f');
% % text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% % title('Processing threads','fontsize',16)
% ylabel('Non-processing Message Satisfied Upload (100%)','fontsize',12)


